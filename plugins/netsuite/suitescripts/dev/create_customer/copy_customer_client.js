/**
*@NApiVersion 2.0
*@NScriptType ClientScript
*/
    
define(['N/currentRecord'], function (currentRecord){

    function pageInit(context) {
        var currentRecord = context.currentRecord;
        
        var companyName = currentRecord.getValue({
            fieldId: 'companyname'
        });
       
      var address = currentRecord.getValue({
            fieldId: 'defaultaddress'
        });
      
      log.debug('companyName', companyName);
      log.debug('address', address);
    }
    
    return {
        pageInit: pageInit
    }
    
});