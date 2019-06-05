package from_ecom.browser.webclient

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

public class RestClientHelper {

    private HTTPBuilder createHttpBuilder(String baseUrl) {
        def builder = new HTTPBuilder(baseUrl)
        builder.ignoreSSLIssues()
        return builder
    }

    def boolean verifyCacheHeadersForRestUrls(String baseUrl,List urlsToVerify,Method method,Map requestHeaders, Closure successhandler) {
        urlsToVerify.each { url->
            println "verifying cache-response Headers for ${baseUrl}${url}"
            def builder = createHttpBuilder("${baseUrl}${url}")
            builder.request(method, {
                if (requestHeaders) {
                    requestHeaders.each{ value,key ->
                        headers[key]= value as String
                    }
                }
                response.success = successhandler
            })
        }
    }

    def boolean verifyCacheHeadersForAjaxRestUrls(String baseUrl,List urlsToVerify,Method method,ContentType contentType,Map requestHeaders, Closure successhandler) {
        urlsToVerify.each { url->
            println "verifying cache-response Headers for ${baseUrl}${url}"
            def builder = createHttpBuilder("${baseUrl}${url}")
            builder.request(method,contentType, {
                if (requestHeaders) {
                    requestHeaders.each{ key,value ->
                        headers["${key}"]= value
                    }
                }
                response.success = successhandler
            })
        }
    }

}
