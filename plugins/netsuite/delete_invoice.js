/**
*@NApiVersion 2.1
*@NScriptType UserEventScript
* @NAmdConfig /SuiteScripts/Libraries/config.json
*/
define(['/SuiteScripts/Utils/httpUtil.js', 'invoiceConfiguration'], (httpUtil, invoiceConfiguration) => {

    const beforeSubmit = context => {
        const invoiceRecord = context.newRecord;
        const invoiceExternalId = invoiceRecord.id;

        deleteInvoice(invoiceExternalId);
    }

    const deleteInvoice = (invoiceExternalId) => {
        const url = invoiceConfiguration.BASE_URL + '/' + invoiceExternalId;
        const errorMessage = 'Could not delete invoice with Id ' + invoiceExternalId;

        httpUtil.sendDeleteRequest(url, errorMessage);
    }

    return {
        beforeSubmit: beforeSubmit
    }
});


