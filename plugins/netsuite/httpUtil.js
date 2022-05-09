/**
* @NApiVersion 2.1
* @NAmdConfig /SuiteScripts/Libraries/config.json
*/
define(['N/https', 'N/encode', 'httpConfiguration'], (https, encode, httpConfiguration) => {

    const sendGetRequest = (url, errorMessage) => {
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

    const sendPutRequest = (url, body, errorMessage) => {
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

    const sendDeleteRequest = (url, errorMessage) => {
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
