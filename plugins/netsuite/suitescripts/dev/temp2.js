/**
 *@NApiVersion 2.0
 *@NScriptType ClientScript
 */
 define(['N/currentRecord','N/https'],function(currentRecord,https){
  function pageInit(context) { 
    var currentRecord = context.currentRecord; 
  
    function success(result) { console.log('Success with value: ' + result) } 
    function failure(reason) { console.log('Failure: ' + reason) } 
  
    dialog.alert({ 
      title: 'Hello World!', 
      message: 'Click OK to continue.' 
    }).then(success).catch(failure); 
  } 
    return {
      pageInit: pageInit
  };
  })
