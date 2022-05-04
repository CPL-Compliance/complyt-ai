/**
*@NApiVersion 2.1
*@NScriptType UserEventScript
*/
define(['N/record', 'N/https', 'N/encode'], (record, https, encode) => {

    const beforeSubmit = context => {
        const invoiceRecord = context.newRecord;
        const invoiceExternalId = invoiceRecord.id;

        deleteInvoice(invoiceExternalId,encode);
    }

    const deleteInvoice = (invoiceExternalId,encode) => {
        const stringInput = "admin:admin";
		const base64EncodedString = encode.convert({
			string: stringInput,
			inputEncoding: encode.Encoding.UTF_8,
			outputEncoding: encode.Encoding.BASE_64
		});

		const auth = 'Basic ' + base64EncodedString;

        const header = {
            'Content-Type':'application/json',
             'Accept':'application/json',
             'Authorization' : auth
        };

        var url='https://complyt-test.herokuapp.com/v1/orders/' + invoiceExternalId;
        try
        {
            https.delete({
                url: url,
                headers: header
            });
        }
        catch(e)
        {
            log.error('ERROR',JSON.stringify(e));
        }
    }

    return {
        beforeSubmit: beforeSubmit
    }
});


