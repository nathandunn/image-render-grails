package org.agr.search

class RenderImageController {

    String imageDirectory = "/var/jbrowse/images"
    // support: http://localhost/jbrowse/overview.html?data=data%2FDanio%20rerio&loc=12:47426569..47433060&tracks=All%20Genes&highlight=&lookupSymbol=chrm3b
    def index() {
        String requestUrl = request.url

        // 1. check cache and return
        String species = getSpecies(requestUrl)
        String location = getLocation(requestUrl)
        File imageFile = new File(imageDirectory+"/"+species+"/"+location)
        if(imageFile.exists()){
            render file: imageFile.bytes, contentType: 'image/png'
            return
        }


        // 2. generate image to the directory
//        phantomjs rasterize.js "http://localhost/jbrowse/overview.html?data=data%2FDanio%20rerio&loc=12:47426569..47433060&tracks=All%20Genes&highlight=&lookupSymbol=chrm3b" jbrowse.png '4800px*2600px' 4 5000
    // convert jbrowse.png -trim +repage jbrowse_trim.png && open jbrowse_trim.png
        File tempFile = File.createTempFile()
        def command = """phantomjs rasterize.js ${requestUrl} ${tempFile.absolutePath} '4800px*2600px' 4 5000"""// Create the String
        def proc = command.execute()                 // Call *execute* on the string
        proc.waitFor()

        def command2 = """convert ${tempFile} +repage ${imageFile}"""// Create the String
        def proc2 = command2.execute()                 // Call *execute* on the string
        proc2.waitFor()

        if(imageFile.exists()){
            render file: imageFile.inputStream, contentType: 'image/png'
        }
        else{
            throw new RuntimeException("Failed to create image")
        }

    }

    String getLocation(String s) {
        return "12:47426569..47433060"
    }

    String getSpecies(String s) {
        return "Danio%20rerio"
    }
//    def renderImage(){
//
//    }
}
