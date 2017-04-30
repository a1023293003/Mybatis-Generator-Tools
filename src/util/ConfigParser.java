package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>ConfigParser</p>
 * <p>针对Properties进行功能拓展的工具类。</p>
 * <pre>
 * 拓展了两个主要功能
 * &lt;1、修改配置文件中已存在的键值对保存的时候保留注释 &gt;
 * &lt;2、修改配置文件时不改变配置文件中的键值对顺序 &gt;
 * </pre>
 * @author 随心
 *
 */
public class ConfigParser {
	
	/**
	 * slf4j日志配置
	 */
	private static final Logger _LOG = LoggerFactory.getLogger(ConfigParser.class);

	/**
	 * 配置文件的路径
	 */
	private String PRO_PATH = "config.properties";
	
	/**
	 * 配置文件对象
	 */
	private Properties config = new Properties();
	
	/**
	 * 注释
	 */
	private LinkedHashMap<String, String> comments = new LinkedHashMap<String, String>();

	/**
	 * 构造方法
	 * @throws FileNotFoundException 配置文件不存在异常
	 * @throws URISyntaxException 经过检查的指示字符串不能解析为 URI 引用的异常
	 */
	public ConfigParser(String path) throws FileNotFoundException, URISyntaxException {
		// 设置配置文件路径
		this.PRO_PATH = this.isExist(path);
		// 读取配置文件
		readProperties();
	}

	/**
	 * 判断配置文件是否存在。
	 * 存在则返回配置文件路径相对路径，失败则抛出异常。
	 * @param path [String]文件路径
	 * @return [String]文件相对路径
	 * @throws URISyntaxException 经过检查的指示字符串不能解析为 URI 引用的异常
	 * @throws FileNotFoundException 配置文件不存在异常
	 */
	private String isExist(String path) throws URISyntaxException, FileNotFoundException {
			// 获取绝对路径
			String absolutePath = path.startsWith("/") ? 
					path : 
					ConfigParser.class.getClassLoader().getResource("").toURI().getPath() + path;
			// 判断文件是否存在，判断是否是文件夹路径
			File file = new File(absolutePath);
			if(!file.exists() || file.isDirectory()) {
				throw new FileNotFoundException("配置文件路径错误！:" + path);
			}
			return path;
	}
	
	/**
	 * 修改配置文件已存在的键值对
	 * @param key [String]键值对的键
	 * @param value [String]键值对key对应的值
	 */
	public void updateToProperties(String key, String value) {
		updateToProperties(key, value, null);
	}
	
	/**
	 * 修改配置文件已存在的键值对
	 * @param key [String]键值对的键
	 * @param value [String]键值对key对应的值
	 * @param comments [String]注释
	 */
	public synchronized void updateToProperties(String key, String value, String comments) {
		PrintWriter writer = null;
		try {
			// 键值对必须存在
			if(!this.config.containsKey(key)) {
				return;
			}
			// 更新配置中的值
			this.config.setProperty(key, value);
			// 修改注释
			if(comments != null) {
				this.comments.put(key, comments == null ? "" : "# " + comments + "\n");
			}
			// 获取输出流，覆盖源文件
			writer = new PrintWriter("config/" + this.PRO_PATH);
			// 遍历Map写入内容
			for(Entry<String, String> entry : this.comments.entrySet()) {
				// 写入注释 + key + value + 换行
				writer.print(entry.getValue() + entry.getKey() + "=" + this.config.getProperty(entry.getKey()) + "\n");
			}
			_LOG.info("修改配置文件{}配置成功！", this.PRO_PATH);
		} catch (Exception e) {
			_LOG.info("修改配置文件{}配置失败！", this.PRO_PATH);
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					// 关闭文件输出流
					writer.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 不读取更新后的文件，只更新内存中的值
		}
	}
	
	/**
	 * 向配置文件末尾新增配置
	 * @param key [String]键值对的键
	 * @param value [String]键值对key对应的值
	 */
	public void appendToProperties(String key, String value) {
		appendToProperties(key, value, null);
	}
	
	/**
	 * 向配置文件末尾新增配置
	 * @param key [String]键值对的键
	 * @param value [String]键值对key对应的值
	 * @param comments [String]注释
	 */
	public synchronized void appendToProperties(String key, String value, String comments) {
		// 键值对必须不存在
		if(this.config.containsKey(key)) {
			return;
		}
		FileOutputStream outStream = null;
		Properties newConfig = new Properties();
		try {
			// 创建文件输出流，true表示在文件末尾新增
			outStream = new FileOutputStream("config/" + this.PRO_PATH, true);
			// 配置文件新增键值对
			newConfig.setProperty(key,  value);
			// 写入配置到内存中
			newConfig.store(new OutputStreamWriter(outStream), comments);
			_LOG.info("新增配置文件{}配置成功！", this.PRO_PATH);
			// 不读取文件信息，只是更新一下内存中的值
			this.config.setProperty(key, value);
			this.comments.put(key, comments == null ? "" : comments);
		} catch (Exception e) {
			_LOG.error("新增配置文件{}配置失败！", this.PRO_PATH);
			e.printStackTrace();
		} finally {
			try {
				if (outStream != null) {
					// 关闭文件输出流
					outStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 读取更新后配置文件的信息
			//readProperties();
		}
	}

	/**
	 * 读取配置文件
	 */
	private synchronized void readProperties() {
		// 输入流数组
		InputStream[] inStreams = new InputStream[3];
		// 读取器的数组
		InputStreamReader[] readers = new InputStreamReader[2];
		// 输入流中的字节数组
		byte[] streamBytes = null;
		try {
			// 读取输入流
			inStreams[0] = ConfigParser.class.getClassLoader().getResourceAsStream(PRO_PATH);
			// 获取输入流字节数组
			streamBytes = getInputStreamBytes(inStreams[0]);
			// 利用reader处理中文乱码问题，用于读取注释
			readers[0] = new InputStreamReader(inStreams[1] = new ByteArrayInputStream(streamBytes), "UTF-8");
			// 利用reader处理中文乱码问题，用于读取键值对
			readers[1] = new InputStreamReader(inStreams[2] = new ByteArrayInputStream(streamBytes), "UTF-8");
			// 利用reader处理中文乱码问题, "UTF-8");
			if(readers[0] != null) {
				// 读取注释信息
				readComments(readers[0]);
				// 读取配置文件内容
				this.config.load(readers[1]);
			}
			_LOG.info("读取配置文件成功！");
		} catch (Exception e) {
			_LOG.error("读取配置文件失败！");
			e.printStackTrace();
		} finally {
				try {
					// 关闭输入流
					for(InputStream is : inStreams) {
						if(is != null) {
							is.close();
						}
					}
					// 关闭读取器
					for(InputStreamReader isr : readers) {
						if(isr != null) {
							isr.close();
						}
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
		}
	}
	
	/**
	 * 获取输入流中的字节
	 * @param inStream [InputStream]待复制输入流
	 * @return [byte[]]复制的输入流的字节数组，复制失败返回null
	 * @throws IOException 
	 */
	private byte[] getInputStreamBytes(InputStream inStream) throws IOException {
		if(inStream == null) return null;
		// 创建字节数组输出流
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// 已读取字节个数
		int total = 0;
		// 当前缓存数组中字节个数
		int limit = 0;
		// 缓存数组
		byte[] buf = new byte[1024];
		// 返回值
		byte[] res = null;
		while((limit = inStream.read(buf)) >= 0) {
			// 把输入流中的内容写入输出流中
			outStream.write(buf, 0, limit);
			// 更新total
			total += limit;
		}
		// 刷新该流的缓冲
		outStream.flush();
		// 读取返回值
		res = outStream.toByteArray();
		// 关闭输出流
		outStream.close();
		if(total > 0) {
			// 返回字节数组
			return res;
		} else {
			_LOG.error("复制流失败");
			return null;
		}
	}
	
	/**
	 * 读取注释
	 * @param reader [InputStreamReader]输入流读取器
	 */
	private synchronized void readComments(Reader reader) {
		readComments0(new LineReader(reader));
	}
	
	private void readComments0(LineReader lr) {
		// 当前读取字符数组的长度
		int limit = 0;
		// 暂存的字符数组
		char[] convtBuf = new char[1024];
		// 存储每行数据的字符串
		String line = null;
		// 存储注释的StringBuffer
		StringBuffer commentsBuf = new StringBuffer();
		// 用来截取key
		String key = null;
		try {
			// 读取一行数据
			while((limit = lr.readLine()) >= 0) {
				// unicode编码转成正常显示的中文
//				line = loadConvert(lr.lineBuf, 0, limit, convtBuf);
				// 如果改行数据为注释
				if(lr.lineBuf[0] == '#' || lr.lineBuf[0] == '!') {
					// unicode编码转成正常显示的中文
					line = loadConvert(lr.lineBuf, 0, limit, convtBuf);
					// 追加到注释缓存字符串中
					commentsBuf.append(line + "\n");
				} else {
					// 读取键值key
					for(int i = 1; i < limit; i ++) {
						// 找到第一个等于号，用来截取key
						if(lr.lineBuf[i] == '=' && lr.lineBuf[i - 1] != '\\') {
							// unicode编码转成正常显示的中文
							key = loadConvert(lr.lineBuf, 0, i, convtBuf);
							// 存储到LinkedHashMap中
							this.comments.put(key, commentsBuf.toString());
							// 清空注释缓存内容
							commentsBuf.setLength(0);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取配置键值对
	 * @param key [String]关键码值
	 * @return [String]关键字值
	 */
	public String getValue(String key) {
		try {
			return config.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 输出配置文件中所有键值对
	 */
	public void printAll() {
		Iterator<String> list = config.stringPropertyNames().iterator();
		while(list.hasNext()) {
			String key = list.next();
			System.out.println("key : " + key + "  value : " + getValue(key));
		}
		System.out.println("注释内容：");
		for(Entry<String, String> entity : comments.entrySet()) {
			System.out.println("key : [" + entity.getKey() + "]  comments : [" + entity.getValue() + "]");
		}
	}
	
	/**
	 * 把\\uXXXX格式的中文字符，转换成中文
	 * 同时把\\转义的字符保留下来
	 * @param in [char[]]输入数据
	 * @param off [int]起始位置
	 * @param len [int]转换字符长度
	 * @param convtBuf [char[]]返回转换结果的数组
	 * @return convtBuf [char[]]返回转换结果的数组
	 */
    private String loadConvert (char[] in, int off, int len, char[] convtBuf) {
        if (convtBuf.length < len) {
            int newLen = len * 2;
            if (newLen < 0) {
                newLen = Integer.MAX_VALUE;
            }
            convtBuf = new char[newLen];
        }
        char aChar;
        char[] out = convtBuf;
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];
                if(aChar == 'u') {
                    // Read the xxxx
                    int value=0;
                    for (int i=0; i<4; i++) {
                        aChar = in[off++];
                        switch (aChar) {
                          case '0': case '1': case '2': case '3': case '4':
                          case '5': case '6': case '7': case '8': case '9':
                             value = (value << 4) + aChar - '0';
                             break;
                          case 'a': case 'b': case 'c':
                          case 'd': case 'e': case 'f':
                             value = (value << 4) + 10 + aChar - 'a';
                             break;
                          case 'A': case 'B': case 'C':
                          case 'D': case 'E': case 'F':
                             value = (value << 4) + 10 + aChar - 'A';
                             break;
                          default:
                              throw new IllegalArgumentException(
                                           "Malformed \\uxxxx encoding.");
                        }
                     }
                    out[outLen++] = (char)value;
                } else {
                    if (aChar == 't') aChar = '\t';
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f';
                    out[outLen++] = aChar;
                }
            } else {
                out[outLen++] = aChar;
            }
        }
        return new String (out, 0, outLen);
    }
	
	/**
	 * stream和reader的缓存读取器
	 * @author 随心
	 *
	 */
	class LineReader {
        public LineReader(InputStream inStream) {
            this.inStream = inStream;
            inByteBuf = new byte[8192];
        }

        public LineReader(Reader reader) {
            this.reader = reader;
            inCharBuf = new char[8192];
        }

        /**
         * 字节缓存数组
         */
        byte[] inByteBuf;
        
        /**
         * 字符缓存数组
         */
        char[] inCharBuf;
        
        /**
         * 读取的每一行数据的缓存数组
         */
        char[] lineBuf = new char[1024];
        
        /**
         * 该次读取的内容长度
         */
        int inLimit = 0;
        
        /*
         * 当前读取的字符的位置
         */
        int inOff = 0;
        
        /**
         * 输入的stream和reader
         */
        InputStream inStream;
        Reader reader;

        /**
         * 读取一行数据，包括注释
         * 修改的JDK源码
         * @return [int]读取到的字符长度
         * @throws IOException
         */
        int readLine() throws IOException {
        	// 返回值，该次读取的总长度
            int len = 0;
            // 每次读取的字符
            char c = 0;

            // 跳过空格，用于去除前导空格
            boolean skipWhiteSpace = true;
            // 附加一行的开始，和skipWhiteSpace一起去除前导空格
            boolean appendedLineBegin = false;
            // 前面是反斜杠
            boolean precedingBackslash = false;
            // 判断是否读取到了\r，windows下回车为：\r\n
            boolean skipLF = false;

            while (true) {
            	// 判断是否读取完了缓存区中的数据
                if (inOff >= inLimit) {
                	// 读取数据到缓存数组中，读取成功返回读取数据长度，读取失败返回-1
                    inLimit = (inStream==null)?reader.read(inCharBuf)
                                              :inStream.read(inByteBuf);
                    // 初始化当前读取的字符的位置
                    inOff = 0;
                    if (inLimit <= 0) {
                    	// 如果读取总长度为0或则该行是注释
                        if (len == 0) {
                            return -1;
                        }
                        // 如果以反斜杠结尾，总长度减一
                        if (precedingBackslash) {
                            len--;
                        }
                        return len;
                    }
                }
                if (inStream != null) {
                    //The line below is equivalent to calling a
                    //ISO8859-1 decoder.
                	// 缓存区中的数据相当于调用了ISO8859-1编码
                    c = (char) (0xff & inByteBuf[inOff++]);
                } else {
                	// reader因为有设置默认编码，所以直接读取
                    c = inCharBuf[inOff++];
                }
                if (skipLF) {
                    skipLF = false;
                    if (c == '\n') {
                        continue;
                    }
                }
                // 判断是否需要滤掉前导空格
                if (skipWhiteSpace) {
                	// 三种空格类型都返回
                    if (c == ' ' || c == '\t' || c == '\f') {
                        continue;
                    }
                    // 如果不是附加一行的开始并且当前字符为换行（windows下，和linux下）则返回
                    if (!appendedLineBegin && (c == '\r' || c == '\n')) {
                        continue;
                    }
                    // 不跳过空格
                    skipWhiteSpace = false;
                    // 不是追加一行的开始
                    appendedLineBegin = false;
                }
                // 不是换行符，加上之前的过滤，判定字符合法
                if (c != '\n' && c != '\r') {
                	// 读取到读取的每一行数据的缓存数组
                    lineBuf[len++] = c;
                    // 如果数组存满了
                    if (len == lineBuf.length) {
                    	// 新建一个长度为当前数组2倍的数组
                        int newLength = lineBuf.length * 2;
                        if (newLength < 0) {
                        	// 当前数组长度不合法则创建一个最大整型长度的数组
                            newLength = Integer.MAX_VALUE;
                        }
                        // 创建数组
                        char[] buf = new char[newLength];
                        // 当前数组的内容写入到新创建的数组中
                        System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
                        // 赋值
                        lineBuf = buf;
                    }
                    //flip the preceding backslash flag
                    // 当前字符为反斜杠
                    if (c == '\\') {
                    	// 取反（好处在意碰到真想输出反斜杠的）
                        precedingBackslash = !precedingBackslash;
                    } else {
                        precedingBackslash = false;
                    }
                }
                // 检测到当前字符为换行符\n或\r
                else {
                    // reached EOL
                	// 当前是注释并且读取总长度为0
                    if (len == 0) {
                        // 省略空格
                        skipWhiteSpace = true;
                        // 当前读取总长度初始化为0
                        len = 0;
                        continue;
                    }
                    // 已经读取完了缓存区的内容
                    if (inOff >= inLimit) {
                    	// 读取内容到缓存区
                        inLimit = (inStream==null)
                                  ?reader.read(inCharBuf)
                                  :inStream.read(inByteBuf);
                        // 初始化读取字符下标为0
                        inOff = 0;
                        // 数据已经读取完毕
                        if (inLimit <= 0) {
                        	// 如果以反斜杠结尾，长度减一
                            if (precedingBackslash) {
                                len--;
                            }
                            return len;
                        }
                    }
                    // 数据尚未读取完毕，并且上一个字符是反斜杠，说明不是注释
                    if (precedingBackslash) {
                    	// 因为反斜杠的原因，所以读取总长度减一
                        len -= 1;
                        //skip the leading whitespace characters in following line
                        // 跳过前导空格
                        skipWhiteSpace = true;
                        // 追加新的一行的开始
                        appendedLineBegin = true;
                        // 上一个不是转义符
                        precedingBackslash = false;
                        // 当前字符为回车
                        if (c == '\r') {
                            skipLF = true;
                        }
                    } else {
                    	// 如果读取到\n，则返回
                        return len;
                    }
                }
            }
        }
    }
	
}
