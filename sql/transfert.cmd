
echo 'Initialisation base de données chantiers2'
dropdb -U postgres -W postgres chantiers2
createdb -U postgres -W postgres --encoding=UTF8 chantiers2

echo 'Création du schéma de la base de données'
psql -U postgres -W postgres -f create_schema.sql chantiers2

echo 'Transfert des données depuis chantiers vers chantiers2'
scriptella

echo 'Transfert Terminé'