/**
* Commandes de lancement manuel
*
* Pour initialiser la base en UTF8
* T:\pgsql_10.4.1\bin\pg_ctl.exe -D T:/pgsql_10.4.1/data initdb -o "-E UTF8"
* Pour lancer le serveur
* T:\pgsql_10.4.1\bin\pg_ctl.exe start -D T:/pgsql_10.4.1/data -l T:/pgsql_10.4.1/log/server.log
* Pour arrêter le serveur
* T:\pgsql_10.4.1\bin\pg_ctl.exe stop -D T:/pgsql_10.4.1/data
* Pour creer le user (apres avoir initialisé et demarre le serveur) :
* T:\pgsql_10.4.1\bin\createuser --interactive --pwprompt 
* indiquer lunchtime/lunctime pour login password, ce n'est pas un super user mais
* il doit pouvoir creer des bases
*
* Fabrication de la base lunchtime
*
* DROP DATABASE IF EXISTS lunchtime;
* Ne pas oublier d'indiquer votre nom
* CREATE DATABASE "lunchtime" WITH OWNER "lunchtime" ENCODING "UTF8" TABLESPACE "pg_default";
* Possible aussi en ligne de commande
* T:\pgsql_10.4.1\bin\createdb.exe -O "lunchtime" -E "UTF8" -D "pg_default" "lunchtime"
**/

DROP TABLE IF EXISTS ltconstraint CASCADE;
DROP TABLE IF EXISTS ltingredient CASCADE;
DROP TABLE IF EXISTS ltmeal_has_ingredient CASCADE;
DROP TABLE IF EXISTS ltmenu_has_meal CASCADE;
DROP TABLE IF EXISTS ltorder_has_quantity CASCADE;
DROP TABLE IF EXISTS ltquantity CASCADE;
DROP TABLE IF EXISTS ltmeal CASCADE;
DROP TABLE IF EXISTS ltmenu CASCADE;
DROP TABLE IF EXISTS ltorder CASCADE;
DROP TABLE IF EXISTS ltuser CASCADE;
DROP TABLE IF EXISTS ltrole CASCADE;
DROP TABLE IF EXISTS ltimage CASCADE;

CREATE TABLE ltimage (
  id SERIAL PRIMARY KEY,
  image_64 TEXT,
  image_path VARCHAR(500),
  image_bin BYTEA,
  is_default BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE ltconstraint (
  id SERIAL PRIMARY KEY,
  order_time_limit TIME NOT NULL,
  maximum_order_per_day INTEGER NOT NULL,
  rate_vat DECIMAL(5,2) NOT NULL  
);

CREATE TABLE ltingredient (
  id SERIAL PRIMARY KEY,
  status SMALLINT NOT NULL,
  label VARCHAR(200) NOT NULL,
  description TEXT,
  image_id INT,
  FOREIGN KEY (image_id) REFERENCES ltimage (id) ON DELETE NO ACTION ON UPDATE NO ACTION  
);

CREATE TABLE ltmeal (
  id SERIAL PRIMARY KEY,
  status SMALLINT NOT NULL,
  label VARCHAR(200) NOT NULL,
  description TEXT,
  price_df DECIMAL(5,2),
  available_for_weeks VARCHAR(300),
  image_id INT,
  FOREIGN KEY (image_id) REFERENCES ltimage (id) ON DELETE NO ACTION ON UPDATE NO ACTION  
);

CREATE TABLE ltmeal_has_ingredient (
  meal_id INTEGER NOT NULL,
  ingredient_id INTEGER NOT NULL,
  PRIMARY KEY (meal_id, ingredient_id),

  FOREIGN KEY (meal_id) REFERENCES ltmeal (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  FOREIGN KEY (ingredient_id) REFERENCES ltingredient (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE ltmenu (
  id SERIAL PRIMARY KEY,
  status SMALLINT NOT NULL,
  label VARCHAR(200) NOT NULL,
  description TEXT,
  price_df DECIMAL(5,2),
  available_for_weeks VARCHAR(300),
  image_id INT,
  FOREIGN KEY (image_id) REFERENCES ltimage (id) ON DELETE NO ACTION ON UPDATE NO ACTION  
);

CREATE TABLE ltmenu_has_meal (
  menu_id INTEGER NOT NULL,
  meal_id INTEGER NOT NULL,
  PRIMARY KEY (menu_id,meal_id),

  FOREIGN KEY (menu_id) REFERENCES ltmenu (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  FOREIGN KEY (meal_id) REFERENCES ltmeal (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE ltuser (
  id SERIAL PRIMARY KEY,
  status SMALLINT NOT NULL,
  password VARCHAR(256) NOT NULL,
  email VARCHAR(300) NOT NULL,
  sex SMALLINT,
  name VARCHAR(256),
  firstname VARCHAR(256),
  wallet DECIMAL(5,2),
  registration_date TIMESTAMP,
  phone VARCHAR(15),
  address VARCHAR(500),
  postal_code VARCHAR(10),
  town VARCHAR(150),
  image_id INT,

  FOREIGN KEY (image_id) REFERENCES ltimage (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  UNIQUE (email)
);

CREATE TABLE ltorder (
  id SERIAL PRIMARY KEY,
  status SMALLINT,
  user_id INTEGER NOT NULL,
  creation_date DATE,
  creation_time TIME,

  FOREIGN KEY (user_id) REFERENCES ltuser (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE ltquantity (
  id SERIAL PRIMARY KEY,
  meal_id INTEGER,
  menu_id INTEGER,
  quantity INTEGER,

  FOREIGN KEY (meal_id) REFERENCES ltmeal (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  FOREIGN KEY (menu_id) REFERENCES ltmenu (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE ltorder_has_quantity (
  order_id INTEGER NOT NULL,
  quantity_id INTEGER NOT NULL,
  PRIMARY KEY (order_id,quantity_id),
  
  FOREIGN KEY (order_id) REFERENCES ltorder (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  FOREIGN KEY (quantity_id) REFERENCES ltquantity (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE ltrole (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  label VARCHAR(200) NOT NULL,
  description TEXT,
  FOREIGN KEY (user_id) REFERENCES ltuser (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);
