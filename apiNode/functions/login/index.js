const fetch = require('node-fetch');
const mysql = require('mysql');
const configImport = require('../config');
const util = require('util');
const callback = require('callback');
const { cpuUsage } = require('process');
const http = require('http');
const { url } = require('inspector');
const { type } = require('os');
const nodemailer = require('nodemailer');

const baseUrl = 'http://127.0.0.1:8080/lunchtime/';
const emptyValue = "Non renseigné(e)";

let con = configImport.connexionSQL;
let messageError = configImport.messageError;
let userError = configImport.userError;
let emailError = configImport.emailError;

let transporter = nodemailer.createTransport({
    service: 'Yahoo',                       // Service utilisé pour l'envoi de l'email
    auth: {
      user: 'projetcantiniere@yahoo.com',   // Email que j'ai créé pour avoir une boite sur Yahoo
      pass: 'uvnvydpvdbzjprum'              // Mot de passe généré par Yahoo pour l'application
    }
});

methods = {
    login : async function(req, res)
    {
        let login = [];
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }
        
        // Récupération des paramètres email et password passés en POST
        emailToFind = req.query.mail; 
        passwordToFind = req.query.password; 

        if(!emailToFind || !passwordToFind)
        {
            res.send("Au moins un des deux paramètres est vide.");
            return false;
        }

        let query = "SELECT * FROM ltuser WHERE email = '"+ emailToFind +"' AND password = '"+ passwordToFind +"';"; // Recherche l'utilisateur pour les données qui correspondent
        con.query(query, function(err, result) {
            if(result[0] != null)
            {
                imageId = result[0].image_id;       
                let queryImg = "SELECT image_64 FROM ltimage WHERE id = "+ imageId +";"; // Recherche l'image qui correspond à l'utilisateur

                con.query(queryImg, function(err, resultImg)
                {
                    res.send({result, resultImg});
                });
            }
            else
                res.send({userError});
        });
    }, 

    checkEmail : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false; 
        }

        // Récupération du paramètre email passé en POST
        emailToFind = req.query.email; 

        let query = "SELECT * FROM ltuser WHERE email = '"+ emailToFind +"';"; // Recherche l'utilisateur pour les données qui correspondent
        con.query(query, function(err, result) {
            if(result[0] != null)
                res.send("L'adresse mail existe déjà dans la base de données.");
            else
                res.send("L'adresse mail n'existe pas dans la base de données.");
        });
    },

    forgotPassword : async function(req, res)
    {
        let isApiAvalaible = await configImport.verification();

        let newPassword = Math.floor(Math.random() * 1000000) + 100000;

        if(!isApiAvalaible)
        {
            res.send(messageError);
            return false;
        }

        email = req.query.email;
        
        let mailOptions = {
            from : 'projetcantiniere@yahoo.com',                // Expéditeur
            to : email,                                         // Destinataire            
            subject : '[Cantinière] - Nouveau mot de passe',    // Sujet
            html : "<p>Veuillez trouver votre nouveau mot de passe : <b>" + newPassword +"</b>, si vous n'êtes pas à l'origine de cette demande, faites attention à vos informations confidentielles.</p>" // Contenu du mail
        }

        let query = "UPDATE ltuser SET password = '" + newPassword + "' WHERE email = '" + email + "';";

        let recoQuery = "SELECT * FROM ltuser WHERE email = '"+ email +"';"; // Cherche si l'utilisateur existe
        con.query(recoQuery, function(err, result) {
            if(result[0] != null)
            {
                con.query(query, function(err, result) {});
                transporter.sendMail(mailOptions, function (err, info) {
                    if(err)
                        console.log("Erreur lors de l'envoi du mail : ", err)
                    else
                        console.log("Email envoyé : " + info.response);
                })
            }         
        })

        res.send("Si l'adresse mail existe, un mail lui a été envoyé (pensez a bien vérifier vos spams).");

    }
}

exports.data = methods;
