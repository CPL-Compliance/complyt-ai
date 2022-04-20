
/**
*@NApiVersion 2.0
*@NScriptType UserEventScript
*/
define(['N/ui/dialog'], function (dialog) {

    function beforeLoad(context) {
        log.debug('beforeLoad')
    }
    
    function beforeSubmit(context) {
        log.debug('beforeSubmit')
    }

    return {
        beforeLoad: beforeLoad,
        beforeSubmit:beforeSubmit
    }
});