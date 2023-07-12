package tests;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import utils.Log;
import utils.ConfReader;
import java.util.Random;
import static io.restassured.RestAssured.given;

public class ApiTest extends Log {

    String key = ConfReader.get("apiKey");
    String token = ConfReader.get("token");
    String baseTrelloURI = ConfReader.get("baseUrl");
    String boardName = ConfReader.get("boardName");
    String cardName_1 = ConfReader.get("firstCartName");
    String cardName_2 = ConfReader.get("secondCartName");
    String listName = ConfReader.get("listName");
    String boardId, IDlist, firstCartId, secondCartId;


    @Test
    public void CreateBoard() {
        logger.info("1. Create trello board");
        String createUrl = baseTrelloURI + "/boards?token=" + token + "&key=" + key + "&name=" + boardName;
        Response response = given().contentType(ContentType.JSON).when().post(createUrl);
        response.then().assertThat().statusCode(200).body("id", Matchers.notNullValue());
        boardId = response.body().jsonPath().getString("id");
        logger.info("Trello board created successfully!");

        logger.info("2. Create trello cards");
        String createListUrl = baseTrelloURI + "/boards/" + boardId + "/lists?name=" + listName + "&key=" + key +
                "&token=" + token;
        Response listResponse = given().contentType(ContentType.JSON).when().post(createListUrl);
        listResponse.then().assertThat().statusCode(200).body("id", Matchers.notNullValue());
        IDlist = listResponse.body().jsonPath().getString("id");
        String createCard_1URI = baseTrelloURI + "/cards?idList=" + IDlist + "&key=" + key + "&token=" + token +
                "&name=" + cardName_1;
        String createCard_2URI = baseTrelloURI + "/cards?idList=" + IDlist + "&key=" + key + "&token=" + token +
                "&name=" + cardName_2;
        Response cartResponse = given().contentType(ContentType.JSON).when().post(createCard_1URI);
        cartResponse.then().assertThat().statusCode(200).body("id", Matchers.notNullValue());
        firstCartId = cartResponse.body().jsonPath().getString("id");
        Response secondCartResponse = given().contentType(ContentType.JSON).when().post(createCard_2URI);
        secondCartResponse.then().assertThat().statusCode(200).body("id", Matchers.notNullValue());
        secondCartId = secondCartResponse.body().jsonPath().getString("id");
        logger.info("Trello cards created successfully!");

        logger.info("3. Update random a card name");
        String[] cardList = {firstCartId, secondCartId};
        int randomCardIndex = new Random().nextInt(2);
        String randomCardId = cardList[randomCardIndex];
        String randomCardUpdateURI = baseTrelloURI + "/cards/" + randomCardId + "?name=updatedCardName" + "&token="
                + token + "&key=" + key;
        Response updateResponse = given().contentType(ContentType.JSON).when().put(randomCardUpdateURI);
        updateResponse.then().assertThat().statusCode(200).body("name", Matchers.equalTo("updatedCardName"));
        logger.info("Cart name updated successfully!");

        logger.info("4. Delete created cards");
        String deleteCard_1URI = baseTrelloURI + "/cards/" + firstCartId + "?token=" + token + "&key=" + key;
        String deleteCard_2URI = baseTrelloURI + "/cards/" + secondCartId + "?token=" + token + "&key=" + key;
        Response firstResponse = given().contentType(ContentType.JSON).when().delete(deleteCard_1URI);
        firstResponse.then().assertThat().statusCode(200);
        Response secondResponse = given().contentType(ContentType.JSON).when().delete(deleteCard_2URI);
        secondResponse.then().assertThat().statusCode(200);
        logger.info("Carts deleted successfully!");

        logger.info("5. Delete trello board");
        String deleteBoardURI = baseTrelloURI + "/boards/" + boardId + "?token=" + token + "&key=" + key;
        Response deleteResponse = given().contentType(ContentType.JSON).when().delete(deleteBoardURI);
        deleteResponse.then().assertThat().statusCode(200);
        logger.info("Trello board deleted successfully");
    }
}
