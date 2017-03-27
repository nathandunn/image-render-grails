package org.agr.search

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(RenderImageController)
class RenderImageControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        given: "urls"
        def inputURL = "http://34.208.22.23/jbrowse/overview.html?data=data%2FCaenorhabditis%20elegans&loc=III:6900568..6903106&tracks=All%20Genes&highlight=&lookupSymbol=brc-2"
        // output species: data%2FCaenorhabditis%20elegans
        // output location: III:6900568..6903106


        when: "we have "
        String location = controller.getLocation(inputURL)
        String species = controller.getSpecies(inputURL)

        then: "we should get the proper data"
        assert species=="Caenorhabditis%20elegans"
        assert location=="III:6900568..6903106"

    }
}
