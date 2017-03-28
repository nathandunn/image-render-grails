package org.agr.search

import org.apache.commons.lang.RandomStringUtils
import org.openqa.selenium.OutputType
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
import net.anthavio.phanbedder.Phanbedder;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;


class RenderImageController {

    final String IMAGE_DIRECTORY = "/opt/agr/jbrowse/overviews"
    final String TEMP_DIRECTORY = "/opt/agr/jbrowse/temp"
    // support: http://localhost/jbrowse/overview.html?data=data%2FDanio%20rerio&loc=12:47426569..47433060&tracks=All%20Genes&highlight=&lookupSymbol=chrm3b

    public static File phantomjs = Phanbedder.unpack(); //Phanbedder to the rescue!

    def overview2() {
        String requestUrl = params.url
        println "request URL [${requestUrl}]"
//        http://34.208.22.23/jbrowse/overview.html?data=data%2FCaenorhabditis%20elegans&loc=III:6900568..6903106&tracks=All%20Genes&highlight=&lookupSymbol=brc-2
        // output URL http://34.208.22.23/jbrowse/overview.html?data=data%2FCaenorhabditis%20elegans&loc=III:6900568..6903106&tracks=All%20Genes&highlight=&lookupSymbol=brc-2
        // output species: data%2FCaenorhabditis%20elegans
        // output location: III:6900568..6903106

        // 1. check cache and return
        String species = getSpecies(requestUrl)
        String location = getLocation(requestUrl)
        // create directory if not existing
        createDirectory(IMAGE_DIRECTORY+"/"+species)
        File imageFile = new File(IMAGE_DIRECTORY+"/"+species+"/"+location+".png")
        if(imageFile.exists()){
            println "Image exists at ${imageFile.absolutePath}"
            render file: imageFile.bytes, contentType: 'image/png'
            return
        }
        println "Image does NOT exist, rendering ${imageFile.absolutePath}"


        DesiredCapabilities dcaps = new DesiredCapabilities();
        dcaps.setCapability("takesScreenshot", true);
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjs.getAbsolutePath());
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        driver.get("https://www.google.com");





        File tempFile = new File("${TEMP_DIRECTORY}+input${RandomStringUtils.randomAlphabetic(10)}.png")
        File screenhotFile = driver.getScreenshotAs(OutputType.FILE)
        println "screenshotFile ${screenhotFile.absolutePath}"


        println "present working directory: " + new File(".").absolutePath
        File currentWorkingDirectory = new File(".")

        println "rendering feature to ${tempFile.absolutePath}"

        def command2 = """convert ${tempFile.absolutePath} -trim +repage ${imageFile.absolutePath}"""// Create the String
        println "Clip command: " +command2
        def proc2 = command2.execute()                 // Call *execute* on the string
        def sout2 = new StringBuilder(), serr2 = new StringBuilder()
        proc2.consumeProcessOutput(sout2, serr2)
        proc2.waitFor()
        println "out> $sout2 err> $serr2"
        println "CONVERTED feature from ${tempFile.absolutePath} to ${imageFile.absolutePath}"

        if(imageFile.exists()){
            println "returning image feature from ${tempFile.absolutePath} to ${imageFile.absolutePath}"
            render file: imageFile.bytes, contentType: 'image/png'
        }
        else{
            throw new RuntimeException("Failed to create image")
        }
    }


    // NEED to encode URLs
    // support: http://localhost:8080/renderImage/overview?url=%22http://34.208.22.23/jbrowse/overview.html?data=data%252FCaenorhabditis%2520elegans%26loc%3DIII%3A6900568..6903106%26tracks%3DAll%2520Genes%26highlight%3D%26lookupSymbol%3Dbrc-2%22
    // support: data%252FCaenorhabditis%2520elegans%26loc%3DIII%3A6900568..6903106%26tracks%3DAll%2520Genes%26highlight%3D%26lookupSymbol%3Dbrc-2
    def overview() {
        String requestUrl = params.url
        println "request URL [${requestUrl}]"
//        http://34.208.22.23/jbrowse/overview.html?data=data%2FCaenorhabditis%20elegans&loc=III:6900568..6903106&tracks=All%20Genes&highlight=&lookupSymbol=brc-2
        // output URL http://34.208.22.23/jbrowse/overview.html?data=data%2FCaenorhabditis%20elegans&loc=III:6900568..6903106&tracks=All%20Genes&highlight=&lookupSymbol=brc-2
        // output species: data%2FCaenorhabditis%20elegans
        // output location: III:6900568..6903106

        // 1. check cache and return
        String species = getSpecies(requestUrl)
        String location = getLocation(requestUrl)
        // create directory if not existing
        createDirectory(IMAGE_DIRECTORY+"/"+species)
        File imageFile = new File(IMAGE_DIRECTORY+"/"+species+"/"+location+".png")
        if(imageFile.exists()){
            println "Image exists at ${imageFile.absolutePath}"
            render file: imageFile.bytes, contentType: 'image/png'
            return
        }
        println "Image does NOT exist, rendering ${imageFile.absolutePath}"


        // 2. generate image to the directory
//        phantomjs rasterize.js "http://localhost/jbrowse/overview.html?data=data%2FDanio%20rerio&loc=12:47426569..47433060&tracks=All%20Genes&highlight=&lookupSymbol=chrm3b" jbrowse.png '4800px*2600px' 4 5000
    // convert jbrowse.png -trim +repage jbrowse_trim.png && open jbrowse_trim.png
//        File tempFile = File.createTempFile("jbrowse-snapshot",".png")
        File tempFile = new File("${TEMP_DIRECTORY}+input${RandomStringUtils.randomAlphabetic(10)}.png")
        println "present working directory: " + new File(".").absolutePath
//        String scriptsDirectory = request.
//        println "scripts directory [${scriptsDirectory}]"
        File currentWorkingDirectory = new File(".")

        println "rendering feature to ${tempFile.absolutePath}"
//        def cmd = ['phantomjs', "${currentWorkingDirectory}/rasterize.js", 'src/groovy scripts/myscript.groovy']
//        def command = """phantomjs --debug=yes --ignore-ssl-errors=true --ssl-protocol=any --web-security=true  ${currentWorkingDirectory.absolutePath}/rasterize.js ${requestUrl} ${tempFile.absolutePath} '4800px*2600px' 4 5000"""// Create the String
//        def command = ["phantomjs","${currentWorkingDirectory.absolutePath}/rasterize.js","${requestUrl}","${tempFile.absolutePath}",'4800px*2600px',"4","5000"]// Create the String
        def command = """./test.sh"""
        println "Screenshot command:\n" +command.join(" ")
        def proc = command.execute()                 // Call *execute* on the string
        def sout = new StringBuilder(), serr = new StringBuilder()
        proc.consumeProcessOutput(sout, serr)
        proc.waitFor()
        println "out> $sout err> $serr"
//        println "StdOut: " + proc.outputStream
//        println "Error: " + proc.errorStream
        println "RENDERED feature to ${tempFile.absolutePath} and exists ${tempFile.exists()}"

        println "converting feature from ${tempFile.absolutePath} to ${imageFile.absolutePath}"
        def command2 = """convert ${tempFile.absolutePath} -trim +repage ${imageFile.absolutePath}"""// Create the String
        println "Clip command: " +command2
        def proc2 = command2.execute()                 // Call *execute* on the string
        def sout2 = new StringBuilder(), serr2 = new StringBuilder()
        proc2.consumeProcessOutput(sout2, serr2)
        proc2.waitFor()
        println "out> $sout2 err> $serr2"
        println "CONVERTED feature from ${tempFile.absolutePath} to ${imageFile.absolutePath}"

        if(imageFile.exists()){
            println "returning image feature from ${tempFile.absolutePath} to ${imageFile.absolutePath}"
            render file: imageFile.bytes, contentType: 'image/png'
        }
        else{
            throw new RuntimeException("Failed to create image")
        }

    }

    def createDirectory(String s) {
        File directory = new File(s)
        if(directory.exists()){
            return directory.isDirectory()
        }
        return directory.mkdir()
    }

    protected String getLocation(String s) {
        String index1String = "loc="
        int index1 = s.indexOf(index1String)
        int index2 = s.indexOf("&",index1)
        return s.substring(index1+index1String.length(),index2)
    }

    protected String getSpecies(String s) {
        String index1String = "data=data%2F"
        int index1 = s.indexOf(index1String)
        int index2 = s.indexOf("&",index1)
        return s.substring(index1+index1String.length(),index2)
    }
//    def renderImage(){
//
//    }
}
