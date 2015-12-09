package www.shanhuang.org.feedr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shan on 11/30/15.
 */
public class Restaurant {
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private String lat;
    private String lng;
    private String price;
    private String reserveUrl;
    private String imageUrl;
    private String rating;

    public Restaurant(JSONObject restaurantData) {
        try {
            this.name = restaurantData.getString("name");
            this.address = restaurantData.getString("address");
            this.city = restaurantData.getString("city");
            this.state = restaurantData.getString("state");
            this.zip = restaurantData.getString("postal_code");
            this.phone = restaurantData.getString("phone");
            this.lat = restaurantData.getString("lat");
            this.lng = restaurantData.getString("lng");
            this.price = restaurantData.getString("price");
            this.reserveUrl = restaurantData.getString("mobile_reserve_url");
            this.imageUrl = restaurantData.getString("image_url");
            this.rating = Integer.parseInt(this.zip) % 2 == 0 ? "3" : "4";

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public String getZip() {
        return this.zip;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getLat() {
        return this.lat;
    }

    public String getLng() {
        return this.lng;
    }

    public String getPrice() {
        return this.price;
    }

    public String getReserveUrl() {
        return this.reserveUrl;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getRating() {
        return this.rating;
    }

    @Override
    public String toString() {
       return this.toString();
    }

    public static void main(String[] args) {
        String data = "{\"total_entries\":15,\"per_page\":100,\"current_page\":1,\"restaurants\":[{\"id\":84985,\"name\":\"Namaste Madreas Cuisine\",\"address\":\"2323 Shattuck Avenue\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5108981291\",\"lat\":37.867237,\"lng\":-122.26727,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=84985\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=84985\",\"image_url\":\"https://www.opentable.com/img/restimages/84985.jpg\"},{\"id\":22675,\"name\":\"Venus - Berkeley\",\"address\":\"2327 Shattuck Avenue\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5105405950\",\"lat\":37.867079,\"lng\":-122.267532,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=22675\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=22675\",\"image_url\":\"https://www.opentable.com/img/restimages/22675.jpg\"},{\"id\":117097,\"name\":\"Build Pizzeria - Berkeley\",\"address\":\"2286 Shattuck Avenue\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5108981839x\",\"lat\":37.867646,\"lng\":-122.268092,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=117097\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=117097\",\"image_url\":\"https://www.opentable.com/img/restimages/117097.jpg\"},{\"id\":97498,\"name\":\"Pathos Restaurant \\u0026 Bar\",\"address\":\"2430 Shattuck Ave\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5109818339x\",\"lat\":37.865204,\"lng\":-122.267794,\"price\":3,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=97498\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=97498\",\"image_url\":\"https://www.opentable.com/img/restimages/97498.jpg\"},{\"id\":94909,\"name\":\"Gecko Gecko\",\"address\":\"2101 Milvia St\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5106654811\",\"lat\":37.870788,\"lng\":-122.270549,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=94909\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=94909\",\"image_url\":\"https://www.opentable.com/img/restimages/94909.jpg\"},{\"id\":89746,\"name\":\"Plearn Thai\",\"address\":\"1923 University Avenue\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5105499999\",\"lat\":37.871768,\"lng\":-122.272389,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=89746\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=89746\",\"image_url\":\"https://www.opentable.com/img/restimages/89746.jpg\"},{\"id\":117349,\"name\":\"Belli Osteria\",\"address\":\"2016 Shattuck Ave\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5107041902x\",\"lat\":37.871908,\"lng\":-122.268374,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=117349\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=117349\",\"image_url\":\"https://www.opentable.com/img/restimages/117349.jpg\"},{\"id\":36430,\"name\":\"Gather\",\"address\":\"2200 Oxford Street\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5108090400\",\"lat\":37.869436,\"lng\":-122.265966,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=36430\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=36430\",\"image_url\":\"https://www.opentable.com/img/restimages/36430.jpg\"},{\"id\":31990,\"name\":\"FIVE\",\"address\":\"2086 Allston Way\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5102256055x\",\"lat\":37.869345,\"lng\":-122.268316,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=31990\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=31990\",\"image_url\":\"https://www.opentable.com/img/restimages/31990.jpg\"},{\"id\":97135,\"name\":\"Joshu-ya Brasserie\",\"address\":\"2441 Dwight Way\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5108485260x\",\"lat\":37.865234,\"lng\":-122.259381,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=97135\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=97135\",\"image_url\":\"https://www.opentable.com/img/restimages/97135.jpg\"},{\"id\":71002,\"name\":\"Julia's at the Berkeley City Club\",\"address\":\"2315 Durant Ave.\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5102801547x\",\"lat\":37.867381,\"lng\":-122.262758,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=71002\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=71002\",\"image_url\":\"https://www.opentable.com/img/restimages/71002.jpg\"},{\"id\":45502,\"name\":\"Revival Bar+Kitchen\",\"address\":\"2102 Shattuck Avenue\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5105499950\",\"lat\":37.871068,\"lng\":-122.26828,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=45502\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=45502\",\"image_url\":\"https://www.opentable.com/img/restimages/45502.jpg\"},{\"id\":50314,\"name\":\"La Note Restaurant Provencal\",\"address\":\"2377 Shattuck Ave.\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5108431525\",\"lat\":37.866174,\"lng\":-122.267525,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=50314\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=50314\",\"image_url\":\"https://www.opentable.com/img/restimages/50314.jpg\"},{\"id\":93874,\"name\":\"Giovanni Restaurant\",\"address\":\"2420 Shattuck Ave\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5108436678\",\"lat\":37.86531,\"lng\":-122.267818,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=93874\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=93874\",\"image_url\":\"https://www.opentable.com/img/restimages/93874.jpg\"},{\"id\":61561,\"name\":\"Mount Everest Restaurant\",\"address\":\"2598 Telegraph Ave.\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5108433951\",\"lat\":37.863381,\"lng\":-122.258888,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=61561\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=61561\",\"image_url\":\"https://www.opentable.com/img/restimages/61561.jpg\"}]}";
        JSONObject restsObj = new JSONObject();
        JSONArray restsArr = new JSONArray();
        ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
        try {
            restsObj = new JSONObject(data);
            restsArr = restsObj.getJSONArray("restaurants");
            for (int i = 0; i < restsArr.length(); i++) {
                Restaurant r = new Restaurant((JSONObject) restsArr.get(i));
                restaurants.add(r);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(restaurants);
    }
}
