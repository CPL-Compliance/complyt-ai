/**
* @NApiVersion 2.1
* @NAmdConfig /SuiteScripts/Config/config.json
*/
define(['N/https', 'N/encode', 'httpConfiguration'], (https, encode, httpConfiguration) => {

    const baseUrl = 'http://demo.complyt.io/';

    const sendGetRequest = (subDomain, errorMessage) => {
        const url = baseUrl + subDomain;
        log.debug('url',url)
        const authHeader = getAuthHeader();
        const headers = {
            'Content-Type': httpConfiguration.CONTENT_TYPE,
            'Accept': httpConfiguration.ACCEPT,
            'Authorization' : authHeader
        };

        try
        {
            var res = https.get({
                url: url,
                headers: headers,
            });
            response = JSON.parse(res.body);
            return response;
        }
        catch(e)
        {
            log.error(errorMessage, JSON.stringify(e));
        }
    }

    const sendPutRequest = (subDomain, body, errorMessage) => {
        const url = baseUrl + subDomain;
        const authHeader = getAuthHeader();
        const headers = {
            'Content-Type': httpConfiguration.CONTENT_TYPE,
            'Accept': httpConfiguration.ACCEPT,
            'Authorization' : authHeader
        };

        try
        {
            var res = https.put({
                url: url,
                headers: headers,
                body: body
            });
            response = JSON.parse(res.body);
            return response;
        }
        catch(e)
        {
            log.error(errorMessage, JSON.stringify(e));
        }
    }

    const sendDeleteRequest = (subDomain, errorMessage) => {
        const url = baseUrl + subDomain;
        const authHeader = getAuthHeader();
        const headers = {
            'Content-Type': httpConfiguration.CONTENT_TYPE,
            'Accept': httpConfiguration.ACCEPT,
            'Authorization' : authHeader
        };

        try
        {
            var res = https.delete({
                url: url,
                headers: headers
            });
            response = JSON.parse(res.body);
            return response;
        }
        catch(e)
        {
            log.error(errorMessage, JSON.stringify(e));
        }
    }

    const getAuthHeader = _ => {
        const stringInput = "admin:admin";
		const base64EncodedString = encode.convert({
			string: stringInput,
			inputEncoding: encode.Encoding.UTF_8,
			outputEncoding: encode.Encoding.BASE_64
		});

		const authHeader = 'Basic ' + base64EncodedString;
        return authHeader;
    }

    return {
        sendGetRequest: sendGetRequest,
        sendPutRequest: sendPutRequest,
        sendDeleteRequest: sendDeleteRequest
    }
});
