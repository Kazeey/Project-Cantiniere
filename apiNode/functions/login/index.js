let graphqlHTTP = require('express-graphql');
let buildSchema = require('graphql');
  
let dailyOrders = [
    {
        name : "pizza",
        price : "17"
    }
]

methods = {
    login : async function(req, res){

    }, 

    forgotPassword : async function(req, res){
        console.log(dailyOrders); // Pour tester pour le débuggage 
        res.send(dailyOrders);
    }
}

exports.data = methods;
