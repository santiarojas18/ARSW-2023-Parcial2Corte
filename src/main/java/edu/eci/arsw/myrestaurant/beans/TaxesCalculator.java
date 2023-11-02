package edu.eci.arsw.myrestaurant.beans;

import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import org.springframework.stereotype.Service;
public interface TaxesCalculator {

	public float getProductTaxes(RestaurantProduct p);
	
}
