$(document).ready(function() {
	// sélection du premier champ du formulaire
	$('#object_dayNumber').focus().select();
	
	// fixer le champ des frais kilométriques au "readonly"
	$('#object_ndfItemKmPrice').attr('readonly', true);
	
	// définition de la fonction de changement du nombre de kilomètres, qui calcule les frais kilométriques
	$('#object_km').change(function() {
		$('#object_ndfItemKmPrice').val(($('#object_km').val() * $('#object_kmprice').val()).toFixed(2));
	});
	
	$('#object_place').change(function() {
		$.post(defaultValuesForPlace({place: this.value}), null, function(ndfitem) {
			$('#object_place').val(ndfitem.place);
			$('#object_trip').val(ndfitem.trip);
			$('#object_km').val(ndfitem.km).change(); // conservation du km + lancement du onchange
		}, 'json');
	});

	// fonction de chargement des valeurs par défaut sur sélection d'une affaire
	$('#object_business').change(function() {
		
		$.post(businessDefaultValuesAction({id: this.value}), null, function(business) {
			$('#object_place').val(business.place);
			$('#object_itemObject').val(business.label);
			$('#object_trip').val(business.trip);
			//$('#object_ndfItemTripPrice').val(business.tripPrice);
			$('#object_km').val(business.km);
			$('#object_km').change(); // recalcul des frais kilométriques
		}, 'json');
	});
});
