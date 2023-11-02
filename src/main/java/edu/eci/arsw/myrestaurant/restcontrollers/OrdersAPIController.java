/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.myrestaurant.restcontrollers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.ProductType;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 *
 * @author hcadavid
 */
@RestController
@RequestMapping(value = "/orders")
public class OrdersAPIController {
    @Autowired
    RestaurantOrderServicesStub ross;

    @RequestMapping(method = RequestMethod.GET)
    @ExceptionHandler(OrderServicesException.class)
    public ResponseEntity<?> handlerGetRequest() {
        Set<Integer> orders = ross.getTablesWithOrders();
        Order currentOrder;
        Gson myGson = new Gson();
        JsonArray finalArray = new JsonArray();
        for (Integer order : orders) {
            currentOrder = ross.getTableOrder(order);
            try {
                JsonObject myJson = new JsonObject();
                Integer bill = ross.calculateTableBill(order);
                myJson.addProperty("table", order);
                JsonArray orderArray = new JsonArray();
                Map<String, Integer> orderMap = currentOrder.getOrderAmountsMap();
                for (String food : orderMap.keySet()) {
                    JsonObject foodObject = new JsonObject();
                    foodObject.addProperty(food, orderMap.get(food));
                    orderArray.add(foodObject);
                }
                myJson.add("order", orderArray);
                myJson.addProperty("total", bill);
                finalArray.add(myJson);
            } catch (OrderServicesException ex) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(myGson.toJson(finalArray), HttpStatus.ACCEPTED);
    }
}
