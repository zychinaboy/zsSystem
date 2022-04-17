打印配置项说明：

print:
  #配置打印机名字,若为 null ,则表示用系统默认的打印机
  printerName: Microsoft Print to PDF
  #支持打印机最大打印队列，即打印缓存中的文件个数
  maxQueuedJobCount: 5
  #打印模板路径，模板文件中{{@photo}}表示照片的占位符号，这是固定死的名字，其它占位符对应数据库的字段名。
  wordtpl: template\录取通知书模板.docx


  photo:
    #照片所在文件夹
    path: F:\code\ZSBmis\kszp
    #照片宽度（像素）
    width: 100
    #照片高度（像素）
    height: 120