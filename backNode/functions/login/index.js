let graphqlHTTP = require('express-graphql');
let buildSchema = require('graphql');
  
methods = {
    login : async function(req, res){

    }, 

    forgotPassword : async function(req, res){
        res.send({response : "test"});
    }
}

exports.data = methods;
