#!/bin/sh

echo "Initialisation base de données chantiers2"
sudo su - postgres
echo "Suppression de la base de données chantiers2"
dropdb chantiers2
echo "Création de la base de données chantiers2"
createdb --encoding=UTF8 chantiers2

echo "Création du schéma de la base de données"
psql -f /home/clorne/workspace/ndf/sql/create_schema.sql chantiers2
logout

echo "Transfert des données depuis chantiers vers chantiers2"
scriptella

echo "Transfert Terminé"