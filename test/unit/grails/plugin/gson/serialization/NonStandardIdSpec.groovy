package grails.plugin.gson.serialization

import com.google.gson.*
import grails.persistence.Entity
import grails.plugin.gson.spring.GsonBuilderFactory
import grails.test.mixin.Mock
import spock.lang.*

@Mock(Publication)
class NonStandardIdSpec extends Specification {

    Gson gson

	void setupSpec() {
		defineBeans {
			gsonBuilder(GsonBuilderFactory) {
				pluginManager = ref('pluginManager')
			}
		}
	}

	void setup() {
		def gsonBuilder = applicationContext.getBean('gsonBuilder', GsonBuilder)
		gson = gsonBuilder.create()
	}

	@Ignore
	@Issue('https://github.com/robfletcher/grails-gson/issues/1')
	void 'can deserialize an existing instance with a non-standard id property'() {
        given:
        def pub1 = new Publication(isbn: '9780670919543', title: 'Zero History').save(failOnError: true)

        and:
        def data = [isbn: '9780670919543']
        def json = gson.toJson(data)

        when:
        def pub2 = gson.fromJson(json, Publication)

        then:
        pub2.id == pub1.id
        pub2.title == pub1.title
    }

	void 'can serialize an instance with a non-standard id property'() {
		given:
		def pub = new Publication(isbn: '9780670919543', title: 'Zero History').save(failOnError: true)

		expect:
		def json = gson.toJsonTree(pub)
		json.isbn.asString == pub.isbn
	}

}

@Entity
class Publication {
    String isbn
    String title
    static mapping = {
        id name: 'isbn', generator: 'assigned'
    }
}