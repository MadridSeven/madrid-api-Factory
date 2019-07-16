package madrid.apiFactory.core.util.source.xml;


import madrid.apiFactory.core.util.exception.SqlSourceException;
import madrid.apiFactory.core.util.source.ISqlSource;
import madrid.apiFactory.core.util.utils.StringUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <code>{@link XmlSqlSource}</code>
 *
 * 通过xml提供SQL语句的实现类
 */
public class XmlSqlSource extends FileAlterationListenerAdaptor implements ISqlSource, InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(XmlSqlSource.class);
	private String sqlLocation = "classpath*:/sqls/**/*.xml"; // sql配置文件所在位置
	private Map<String, String> sqlMap = new ConcurrentHashMap<String, String>();
	private String sqlFolder = null;

	/**
	 * 修正在内网无法访问xml属性文件dtd问题
	 */
	private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";
	private static final String PROPS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<!-- DTD for properties -->"
			+ "<!ELEMENT properties ( comment?, entry* ) >" + "<!ATTLIST properties" + " version CDATA #FIXED \"1.0\">"
			+ "<!ELEMENT comment (#PCDATA) >" + "<!ELEMENT entry (#PCDATA) >" + "<!ATTLIST entry "
			+ " key CDATA #REQUIRED>";

	/**
	 * @return the sqlLocation
	 */
	public String getSqlLocation() {
		return sqlLocation;
	}

	/**
	 * @param sqlLocation
	 *            the sqlLocation to set
	 */
	public void setSqlLocation(String sqlLocation) {
		this.sqlLocation = sqlLocation;
	}

	public XmlSqlSource() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bingo.dao.source.ISqlSource#getSql(java.lang.String)
	 */
	public String getSql(String sqlId) {
		return sqlMap.get(sqlId);
	}

	/**
	 * 从制定路径读取sql文件
	 */
	public void loadSqls() {
		if (this.sqlLocation == null || sqlLocation.trim().equals("")) {
			throw new SqlSourceException("sql files location must set.");
		}
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = null;
		try {
			resources = resourcePatternResolver.getResources(this.sqlLocation);
			InputStream inputStream = null;
			for (Resource resource : resources) {
				try {
					URL url = resource.getURL();

					if (logger.isDebugEnabled()) {
						logger.debug("found {} resource '{}'", url.getProtocol(), url.toExternalForm());
					}
					// 如果sql是来自于文件系统
					if (ResourceUtils.URL_PROTOCOL_FILE.equalsIgnoreCase(url.getProtocol())) {
						if (sqlFolder == null) {
							findMonitroFolder(resource.getFile());
						}
						inputStream = new FileInputStream(resource.getFile());
					} else {
						inputStream = resource.getInputStream();
					}
					if (resource.getFilename().endsWith(".xml")) {
						loadFromXml(inputStream, resource.getFilename(), false);
					}
				} finally {
					IOUtils.closeQuietly(inputStream);
				}
			}
		} catch (Exception e) {
			logger.error("读取资源文件时出现错误,返回null ...", e);
		}
	}

	/**
	 * 根据资源文件定位受监控的sql根目录
	 */
	private void findMonitroFolder(File file) {
		while (file.getParentFile() != null) {
			File sqlFolderFile = file.getParentFile();
			if (sqlLocation.contains(sqlFolderFile.getName())) {
				sqlFolder = sqlFolderFile.getAbsolutePath();
				break;
			} else {
				file = sqlFolderFile;
			}
		}
	}

	/**
	 * 从文件中读取sql语句到缓存中
	 */
	private void loadFromXml(InputStream inputStream, String fileName, boolean enabledOverride)
			throws ParserConfigurationException, SAXException, IOException {
		String subfix = getSqlIdSubfix(fileName);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		builder.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				if (systemId.equals(PROPS_DTD_URI)) {
					InputSource inputStream = new InputSource(new StringReader(PROPS_DTD));
					inputStream.setSystemId(PROPS_DTD_URI);
					return inputStream;
				}
				throw new SAXException("Invalid system identifier: " + systemId);
			}
		});
		Document document = builder.parse(inputStream);

		NodeList sqls = document.getElementsByTagName("entry");
		for (int i = 0; i < sqls.getLength(); i++) {
			Node element = sqls.item(i);
			String key = element.getAttributes().getNamedItem("key").getNodeValue();
			// 处理多数据库支持
			key = key + subfix;
			String value = element.getTextContent();
			// 处理编码
			// value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
			if (sqlMap.containsKey(key) && !enabledOverride) {
				throw new SqlSourceException(String.format("duplicate key '%s' found in '%s'", key, fileName));
			}
			sqlMap.put(key, value);
		}
	}

	/**
	 * 取得sql文件对应的数据库类型，进而得出sqlId应该的后缀
	 */
	private String getSqlIdSubfix(String fileName) {
		return "";
	}
//	private String getSqlIdSubfix(String fileName) {
//		String dbDialect = extractDbDialectName(fileName);
//		if (!StringUtils.isEmpty(dbDialect) && BaseDialect.findDbDialect(dbDialect)) {
//			return "$" + dbDialect.toLowerCase();
//		}
//		return "";
//	}

	/**
	 * 通过sql文件名识别数据库类型
	 */
//	private String extractDbDialectName(String fileName) {
//		int index = fileName.lastIndexOf(".");
//
//		if (index > 0) {
//			String name = fileName.substring(0, index);
//
//			index = name.lastIndexOf(".");
//
//			if (index > 0) {
//				return name.substring(index + 1);
//			}
//		}
//
//		return null;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.io.monitor.FileAlterationListenerAdaptor#onFileChange(
	 * java.io.File)
	 */
	@Override
	public void onFileChange(File file) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("file:%s has changed, reload sql.", file.getName()));
		}
		doOnFileChange(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.io.monitor.FileAlterationListenerAdaptor#onFileCreate(
	 * java.io.File)
	 */
	@Override
	public void onFileCreate(File file) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("file:%s has created, reload sql.", file.getName()));
		}
		doOnFileChange(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.io.monitor.FileAlterationListenerAdaptor#onFileDelete(
	 * java.io.File)
	 */
	@Override
	public void onFileDelete(File file) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("file:%s has deleted, reload sql.", file.getName()));
		}
		doOnFileChange(file);
	}

	private void doOnFileChange(File file) {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			loadFromXml(inputStream, file.getName(), true);
			//clear掉sql解析后的缓存
			//TableMapper.clearTableMapping();
		} catch (Exception e) {
			logger.error(String.format("cause error on reload sql from file: %s", file.getName()), e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		loadSqls();
		if (StringUtils.isNotEmpty(sqlFolder)) {
			FileAlterationObserver observer = new FileAlterationObserver(sqlFolder,
					FileFilterUtils.suffixFileFilter(".xml", IOCase.INSENSITIVE));
			observer.addListener(this);
			FileAlterationMonitor monitor = new FileAlterationMonitor(5000, observer);
			monitor.start();
		}
	}
}
