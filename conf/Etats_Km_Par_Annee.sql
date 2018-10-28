select n.ndf_date, sum(ni.km) from t_ndf n
join t_ndf_item ni on ni.ndf_id=n.id
where n.ndf_date >= '2013-01-01' and n.ndf_date < '2014-01-01'
group by n.ndf_date
order by n.ndf_date


