# rb-image-processor
A batch image processor that takes an image API url and a directory path, retrieves the image data and transforms it in a structured set of HTML pages. The hierarchy of HTML pages is written to the specified output directory.

Usage
The build/install/bin subfolder contains ready-to-use start scripts, e.g. for Windows run

`rb-image-processor http://take-home-test.herokuapp.com/api/ C:\Users\<username>\Desktop\htmlPages`

Development

As this is a gradle project you can also run
`gradle run -PappArgs="['http://take-home-test.herokuapp.com/api/', 'C:\\Users\\Ferdinand\\Desktop\\outputDir']"`

Architecture