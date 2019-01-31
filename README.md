# LuBan
1.使用方法在根目录中的build.gradle 
	
  allprojects {
  
		repositories {
    
			...
      
			maven { url 'https://jitpack.io' }
      
		}
    
	}
   
  2.加入依赖
  
  dependencies {
  
	        implementation 'com.github.zhihaoliang:Sheetdialog:v1.0'
          
	}
  
  3.使用方法
  Luban.with(this). //Context  设置Context
  
     setFileSize(100).//设置压缩到100k以内
     
     setFocusAlpha(true).//是否对图片显示质量进行压缩
     
     setLongSide(1080).//设置图片压缩的最大变，如果压缩到图片的长边小于或等于设置的longside 会继续对图片的大小进行压缩
     
     setOriginalPaths(ORGIN_PATH).//设置图片原始路径 ，接受String、string[]、List<String>
     
     setOnCompressListener(this).//压缩后的监听
     
     setTargetDir(initTargetDir()).//压缩后生成文件所在的路径
     
     launch();
  
  
  3.监听的解释
  
    /**
     * 当压缩开始调用的方法
     
     */
     
    void onCompressStart();
    
    /**
    
     * 压缩成功的回调
     
     * @param list 表示压缩的后的结果
     
     */
     
    void onCompressSuccess(List<CompressBean> list);
    
    /**
    
     * 压缩失败的回调
     
     * @param list 表示压缩的后的结果
     
     * @param erroMsg 失败的原因
     
     */
     
    void onCompressError(List<CompressBean> list,String erroMsg);
    
    3.CompressBean的解释
     /**
     * 图片未压缩的路径
     
     */
     
    private String originalPath;
    
    /**
    
     * 压缩后的图片路径
     
     */
    private String compressPath;
    
    /**
    
     * 压缩失败的原因
     
     */
     
    private String erroMsg;
    
    
