#/bin/bash

# server checks cache and then delivers image or not 

phantomjs rasterize.js "http://localhost/jbrowse/overview.html?data=data%2FDanio%20rerio&loc=12:47426569..47433060&tracks=All%20Genes&highlight=&lookupSymbol=chrm3b" jbrowse.png '4800px*2600px' 4 5000 && convert jbrowse.png -trim +repage jbrowse_trim.png && open jbrowse_trim.png 

