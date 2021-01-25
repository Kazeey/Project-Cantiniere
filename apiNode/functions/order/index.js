const fetch = require('node-fetch');
const configImport = require('../config');
const request = require('request');
const nodemailer = require('nodemailer');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';
let con = configImport.connexionSQL;

methods = {
    getOrderByUserToday : async function(req, res) 
    {
        let login = [];
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }
    },

    getOrderByUser : async function(req, res)
    {

        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        // Récupération du paramètre userId passe en POST
        userIdToFind = req.body.userId; 

        let orderList;
        let isException = [];

        // Récupère les données de l'utilisateur ciblé
        await fetch(baseUrl +  "order/findallforuser/" + userIdToFind)
        .then(response => response.json())
        .then(data => {
            if(data.exceptionMessage)
            {
                // Vérifie s'il y a une exception (donc si une commande n'est pas trouvée)
                isException.push(this.data.exceptionMessage);
            }
            else
            {
                // Si une commande est trouvé réinitialise le tableau d'exception pour pouvoir réitérer la fonction.
                isException = [];

                this.orderList = data;
            }
        })

        if(isException.length != 0)
        {
            res.send(configImport.menuError);
            return false; 
        }
        res.send(this.orderList);
    },

    getAllOrders : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let orderList;
        
        // Récupère chaque commandes de la base
        await fetch(baseUrl +  "order/findall")
        .then(response => response.json())
        .then(data => {
                orderList = data;
        })
        
        res.send(orderList);
    },

    getDailyOrder : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let orderList;
        let status = req.query.status;
        let beginDate = req.query.beginDate;
        let endDate = req.query.endDate

        await fetch(baseUrl +  "order/findallbetweendateinstatus?status="+ status +"&beginDate="+ beginDate +"&endDate="+ endDate +"")
        .then(response => response.json())
        .then(data => {
            orderList = data;
        })

        res.send(orderList);
    },

    confirmOrder : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        let orderId = req.body.orderId;
        let verifNotif = req.body.verifNotif;
        let userId = req.body.userId;

        let order = {
            orderId : orderId,
            constraintId : 1
        }

        request.patch({
            headers: {'content-type' : 'application/json'},
            url: baseUrl + "order/deliverandpay/" + JSON.stringify(order.orderId) + "/" +JSON.stringify(order.constraintId),
            body: JSON.stringify(order)
        },
        function(error, response, body) {
            console.log(error);
        });    

        
        let transporter = nodemailer.createTransport({
            service: 'Yahoo',                       // Service utilisé pour l'envoi de l'email
            auth: {
            user: 'projetcantiniere@yahoo.com',   // Email que j'ai créé pour avoir une boite sur Yahoo
            pass: 'uvnvydpvdbzjprum'              // Mot de passe généré par Yahoo pour l'application
            }
        });

        if (verifNotif == "true")
        {
            let query = "SELECT email FROM ltuser WHERE id =" + userId;
            con.query(query, function(err, result) {
                if(result[0] != null)
                {
                    let mailOptions = {
                        from : 'projetcantiniere@yahoo.com',                   // Expéditeur
                        to : result[0].email,                                        // Destinataire            
                        subject : '[Cantinière] - Confirmation de commande',   // Sujet
                        html : "Votre commande pour aujourd'hui a été validée" // Contenu du mail
                    }

                    transporter.sendMail(mailOptions, function (err, info) {
                        if(err)
                            console.log("Erreur lors de l'envoi du mail : ", err)
                        else
                            console.log("Email envoyé : " + info.response);
                    })
                }
            });
        }
        
        res.send(true);
    }
}

exports.data = methods;