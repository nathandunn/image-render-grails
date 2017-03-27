package org.agr.search

class RenderImageController {

    final String IMAGE_DIRECTORY = "/opt/agr/jbrowse/overviews"
    // support: http://localhost/jbrowse/overview.html?data=data%2FDanio%20rerio&loc=12:47426569..47433060&tracks=All%20Genes&highlight=&lookupSymbol=chrm3b

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
        File tempFile = File.createTempFile("jbrowse-snapshot",".png")

        println "present working directory: " + new File(".").absolutePath

        println "rendering feature to ${tempFile.absolutePath}"
        def command = """phantomjs rasterize.js ${requestUrl} ${tempFile.absolutePath} '4800px*2600px' 4 5000"""// Create the String
        println "Screenshot command: " +command
        def proc = command.execute()                 // Call *execute* on the string
        proc.waitFor()
        println "StdOut: " + proc.outputStream
        println "Error: " + proc.errorStream
        println "RENDERED feature to ${tempFile.absolutePath}"

        println "converting feature from ${tempFile.absolutePath} to ${imageFile}"
        def command2 = """convert ${tempFile} +repage ${imageFile}"""// Create the String
        println "Clip command: " +command2
        def proc2 = command2.execute()                 // Call *execute* on the string
        proc2.waitFor()
        println "CONVERTED feature from ${tempFile.absolutePath} to ${imageFile}"

        if(imageFile.exists()){
            println "returning image feature from ${tempFile.absolutePath} to ${imageFile}"
            render file: imageFile.inputStream, contentType: 'image/png'
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
