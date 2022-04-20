/**
*@NApiVersion 2.0
*@NScriptType ClientScript
*/
    
define(['N/ui/dialog'], function (dialog){

    function pageInit(context) {
        dialog.alert({
            title: 'pageInit',
            message: 'pageInit' 
        })
    }

    function validateField(context) {
        dialog.alert({
            title: 'validateField',
            message: 'validateField' 
        })
    }
    
    function fieldChanged(context) {
        dialog.alert({
            title: 'fieldChanged',
            message: 'fieldChanged' 
        })
    }

    function postSourcing(context) {
        dialog.alert({
            title: 'postSourcing',
            message: 'postSourcing' 
        })
    }
    
    function lineInit(context) {
        dialog.alert({
            title: 'lineInit',
            message: 'lineInit' 
        })
    }

    function validateLine(context) {
        dialog.alert({
            title: 'validateLine',
            message: 'validateLine' 
        })
    }

    function validateInsert(context) {
        dialog.alert({
            title: 'validateInsert',
            message: 'validateInsert' 
        })
    }

    function validateDelete(context) {
        dialog.alert({
            title: 'validateDelete',
            message: 'validateDelete' 
        })
    }
    
    function recalc(context) {
        dialog.alert({
            title: 'recalc',
            message: 'recalc' 
        })
    }

    function sublistChanged(context) {
        dialog.alert({
            title: 'sublistChanged',
            message: 'sublistChanged' 
        })
    }

    function saveRecord(context) {
        dialog.alert({
            title: 'saveRecord',
            message: 'saveRecord' 
        })
    }
    
    return {
        pageInit: pageInit,
        validateField:validateField,
        fieldChanged:fieldChanged,
        postSourcing:postSourcing,
        lineInit:lineInit,
        validateLine:validateLine,
        validateInsert:validateInsert,
        validateDelete:validateDelete,
        recalc:recalc,
        sublistChanged:sublistChanged,
        saveRecord:saveRecord
    }

});