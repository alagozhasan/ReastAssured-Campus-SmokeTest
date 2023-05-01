package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CountryTest {
    Faker faker = new Faker();
    Map<String, String> countryInfo = new HashMap<>();

    String countryName = "";
    RequestSpecification reqSpec;
    String countryID;

    @BeforeClass
    void Login() {
//            "username": "turkeyts",
//            "password": "TechnoStudy123",
//            "rememberMe": true
        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        baseURI = "https://test.mersys.io";
        Cookies cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)
                        .when()
                        .log().uri()
                        .post("/auth/login")

                        .then()
//                        .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()//tüm cookileri alır
                ;
        reqSpec = new RequestSpecBuilder()
                .addCookies(cookies)//tüm cookileri aldım ve gönderdim
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    void CreateCountry() {
        countryName += faker.country().name() + "-" + faker.number().digits(4);
        countryInfo.put("name", countryName);
        countryInfo.put("code", faker.address().countryCode());
        countryID =
                given()
                        .spec(reqSpec)
                        .body(countryInfo)
                        .when()
                        .log().uri()
                        .post("/school-service/api/countries")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
        System.out.println("countryID = " + countryID);
    }

    @Test(dependsOnMethods = "CreateCountry")
    void CreateCountryNegative() {

//     String message=
        given()
                .spec(reqSpec)
                .body(countryInfo)
                .when()
                .log().uri()
                .post("/school-service/api/countries")

                .then()
                .log().body()
                .statusCode(400)
                //veya
                .body("message", containsString("already exists"));
//             .extract().path("message")

        ;
//        System.out.println(message);
        System.out.println(countryInfo);
//        Assert.assertTrue(message.contains("already exists"));

    }

    @Test(dependsOnMethods = "CreateCountryNegative")
    void UpdateCountry() {
        Map<String, String> editedCountry = new HashMap<>();
        editedCountry.put("id", countryID);
        countryName += "hasan";
        editedCountry.put("name", countryName);
        given()
                .spec(reqSpec)
                .body(editedCountry)
                .when()
                .log().uri()
                .put("/school-service/api/countries")

                .then()
                .log().body()
                .statusCode(200)
                //veya
                .body("name", equalTo(countryName));

    }

    @Test(dependsOnMethods = "UpdateCountry")
    void DeleteCountry() {

        given()
                .spec(reqSpec)
                .when()
                .log().uri()
                .pathParam("countryID", countryID)
                .delete("/school-service/api/countries/{countryID}")

                .then()
                .log().body()
                .statusCode(200)
                //veya
              ;
    }

    @Test(dependsOnMethods = "DeleteCountry")
    void DeleteCountryNegative() {

        given()
                .spec(reqSpec)
                .when()
                .log().uri()
                .pathParam("countryID", countryID)
                .delete("/school-service/api/countries/{countryID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Country not found"))
        //veya
        ;
    }
// TODO : CtizenShip  API Automation nı yapınız (create, createNegative, update, delete, deleteNegative)

}
