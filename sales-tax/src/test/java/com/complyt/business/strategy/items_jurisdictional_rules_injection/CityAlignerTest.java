package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CityAlignerTest {

    @Test
    void getCityValue_validIdenticalCityName_returnAlignCity() {
        assertEquals("Chicago", CityAligner.getCityValue("Chicago"));
        assertEquals("Chicago", CityAligner.getCityValue("CHICAGO"));

        assertEquals("Arvada", CityAligner.getCityValue("Arvada"));
        assertEquals("Arvada", CityAligner.getCityValue("Aravada"));
        assertEquals("Arvada", CityAligner.getCityValue("Aravda"));
        assertEquals("Arvada", CityAligner.getCityValue("ARVADA"));

        assertEquals("Aspen", CityAligner.getCityValue("Aspen"));
        assertEquals("Aspen", CityAligner.getCityValue("Aspn"));
        assertEquals("Aspen", CityAligner.getCityValue("ASPEN"));

        assertEquals("Aurora", CityAligner.getCityValue("Aurora"));
        assertEquals("Aurora", CityAligner.getCityValue("Aorora"));
        assertEquals("Aurora", CityAligner.getCityValue("AURORA"));

        assertEquals("Avon", CityAligner.getCityValue("Avon"));
        assertEquals("Avon", CityAligner.getCityValue("AVON"));

        assertEquals("Black Hawk", CityAligner.getCityValue("Black Hawk"));
        assertEquals("Black Hawk", CityAligner.getCityValue("Black Haok"));
        assertEquals("Black Hawk", CityAligner.getCityValue("BLACK HAWK"));

        assertEquals("Boulder", CityAligner.getCityValue("Boulder"));
        assertEquals("Boulder", CityAligner.getCityValue("BOULDER"));

        assertEquals("Breckenridge", CityAligner.getCityValue("Breckenridge"));
        assertEquals("Breckenridge", CityAligner.getCityValue("BRECKENRIDGE"));

        assertEquals("Brighton", CityAligner.getCityValue("Brighton"));
        assertEquals("Brighton", CityAligner.getCityValue("Brigton"));
        assertEquals("Brighton", CityAligner.getCityValue("BRIGHTON"));

        assertEquals("Broomfield", CityAligner.getCityValue("Broomfield"));
        assertEquals("Broomfield", CityAligner.getCityValue("Bromfield"));
        assertEquals("Broomfield", CityAligner.getCityValue("Broomfild"));
        assertEquals("Broomfield", CityAligner.getCityValue("BROOMFIELD"));

        assertEquals("Carbondale", CityAligner.getCityValue("Carbondale"));
        assertEquals("Carbondale", CityAligner.getCityValue("Carbondle"));
        assertEquals("Carbondale", CityAligner.getCityValue("CARBONDALE"));

        assertEquals("Castle Pines", CityAligner.getCityValue("Castle Pines"));
        assertEquals("Castle Pines", CityAligner.getCityValue("Castle Pins"));
        assertEquals("Castle Pines", CityAligner.getCityValue("Casle Pines"));
        assertEquals("Castle Pines", CityAligner.getCityValue("CASTLE PINES"));

        assertEquals("Castle Rock", CityAligner.getCityValue("Castle Rock"));
        assertEquals("Castle Rock", CityAligner.getCityValue("Casle Rock"));
        assertEquals("Castle Rock", CityAligner.getCityValue("Castl Rock"));
        assertEquals("Castle Rock", CityAligner.getCityValue("CASTLE ROCK"));

        assertEquals("Centennial", CityAligner.getCityValue("Centennial"));
        assertEquals("Centennial", CityAligner.getCityValue("Centenial"));
        assertEquals("Centennial", CityAligner.getCityValue("CENTENNIAL"));

        assertEquals("Central City", CityAligner.getCityValue("Central City"));
        assertEquals("Central City", CityAligner.getCityValue("CENTRAL CITY"));

        assertEquals("Cherry Hills Village", CityAligner.getCityValue("Cherry Hills Village"));
        assertEquals("Cherry Hills Village", CityAligner.getCityValue("Chery Hills Village"));
        assertEquals("Cherry Hills Village", CityAligner.getCityValue("Cherry Hills Villge"));
        assertEquals("Cherry Hills Village", CityAligner.getCityValue("Cherry Hills Vilage"));
        assertEquals("Cherry Hills Village", CityAligner.getCityValue("CHERRY HILLS VILLAGE"));

        assertEquals("Colorado Springs", CityAligner.getCityValue("Colorado Springs"));
        assertEquals("Colorado Springs", CityAligner.getCityValue("Colorado Spring"));
        assertEquals("Colorado Springs", CityAligner.getCityValue("COLORADO SPRINGS"));

        assertEquals("Commerce City", CityAligner.getCityValue("Commerce City"));
        assertEquals("Commerce City", CityAligner.getCityValue("Comerce City"));
        assertEquals("Commerce City", CityAligner.getCityValue("COMMERCE CITY"));

        assertEquals("Cortez", CityAligner.getCityValue("Cortez"));
        assertEquals("Cortez", CityAligner.getCityValue("CORTEZ"));

        assertEquals("Craig", CityAligner.getCityValue("Craig"));
        assertEquals("Craig", CityAligner.getCityValue("Crage"));
        assertEquals("Craig", CityAligner.getCityValue("CRAIG"));

        assertEquals("Mt Crested Butte", CityAligner.getCityValue("Crested Butte"));
        assertEquals("Mt Crested Butte", CityAligner.getCityValue("Crestd Butte"));
        assertEquals("Mt Crested Butte", CityAligner.getCityValue("CRESTED BUTTE"));

        assertEquals("Dacono", CityAligner.getCityValue("Dacono"));
        assertEquals("Dacono", CityAligner.getCityValue("Daconno"));
        assertEquals("Dacono", CityAligner.getCityValue("DACONO"));

        assertEquals("Delta", CityAligner.getCityValue("Delta"));
        assertEquals("Delta", CityAligner.getCityValue("DELTA"));

        assertEquals("Denver", CityAligner.getCityValue("Denver"));
        assertEquals("Denver", CityAligner.getCityValue("DENVER"));

        assertEquals("Durango", CityAligner.getCityValue("Durango"));
        assertEquals("Durango", CityAligner.getCityValue("DURANGO"));

        assertEquals("Edgewater", CityAligner.getCityValue("Edgewater"));
        assertEquals("Edgewater", CityAligner.getCityValue("Edgwater"));
        assertEquals("Edgewater", CityAligner.getCityValue("Egewater"));
        assertEquals("Edgewater", CityAligner.getCityValue("EDGEWATER"));

        assertEquals("Englewood", CityAligner.getCityValue("Englewood"));
        assertEquals("Englewood", CityAligner.getCityValue("Englwood"));
        assertEquals("Englewood", CityAligner.getCityValue("Englewod"));
        assertEquals("Englewood", CityAligner.getCityValue("ENGLEWOOD"));

        assertEquals("Evans", CityAligner.getCityValue("Evans"));
        assertEquals("Evans", CityAligner.getCityValue("Evns"));
        assertEquals("Evans", CityAligner.getCityValue("EVANS"));

        assertEquals("Federal Heights", CityAligner.getCityValue("Federal Heights"));
        assertEquals("Federal Heights", CityAligner.getCityValue("Federal Heigts"));
        assertEquals("Federal Heights", CityAligner.getCityValue("FEDERAL HEIGHTS"));

        assertEquals("Fort Collins", CityAligner.getCityValue("Fort Collins"));
        assertEquals("Fort Collins", CityAligner.getCityValue("Ft Collins"));
        assertEquals("Fort Collins", CityAligner.getCityValue("Fort Colins"));
        assertEquals("Fort Collins", CityAligner.getCityValue("FORT COLLINS"));

        assertEquals("Frisco", CityAligner.getCityValue("Frisco"));
        assertEquals("Frisco", CityAligner.getCityValue("FRISCO"));

        assertEquals("Glendale", CityAligner.getCityValue("Glendale"));
        assertEquals("Glendale", CityAligner.getCityValue("GLENDALE"));

        assertEquals("Glenwood Springs", CityAligner.getCityValue("Glenwood Springs"));
        assertEquals("Glenwood Springs", CityAligner.getCityValue("Glenwood Spring"));
        assertEquals("Glenwood Springs", CityAligner.getCityValue("GLENWOOD SPRINGS"));

        assertEquals("Golden", CityAligner.getCityValue("Golden"));
        assertEquals("Golden", CityAligner.getCityValue("GOLDEN"));

        assertEquals("Grand Junction", CityAligner.getCityValue("Grand Junction"));
        assertEquals("Grand Junction", CityAligner.getCityValue("GRAND JUNCTION"));

        assertEquals("Greeley", CityAligner.getCityValue("Greeley"));
        assertEquals("Greeley", CityAligner.getCityValue("Greley"));
        assertEquals("Greeley", CityAligner.getCityValue("GREELEY"));

        assertEquals("Greenwood Village", CityAligner.getCityValue("Greenwood Village"));
        assertEquals("Greenwood Village", CityAligner.getCityValue("Greenwod Village"));
        assertEquals("Greenwood Village", CityAligner.getCityValue("Grenwood Village"));
        assertEquals("Greenwood Village", CityAligner.getCityValue("GREENWOOD VILLAGE"));

        assertEquals("Gunnison", CityAligner.getCityValue("Gunnison"));
        assertEquals("Gunnison", CityAligner.getCityValue("Gunison"));
        assertEquals("Gunnison", CityAligner.getCityValue("GUNNISON"));

        assertEquals("Gypsum", CityAligner.getCityValue("Gypsum"));
        assertEquals("Gypsum", CityAligner.getCityValue("Gipsum"));
        assertEquals("Gypsum", CityAligner.getCityValue("GYPSUM"));

        assertEquals("La Junta", CityAligner.getCityValue("La Junta"));
        assertEquals("La Junta", CityAligner.getCityValue("LA JUNTA"));

        assertEquals("Lafayette", CityAligner.getCityValue("Lafayette"));
        assertEquals("Lafayette", CityAligner.getCityValue("Lafayete"));
        assertEquals("Lafayette", CityAligner.getCityValue("LAFAYETTE"));

        assertEquals("Lakewood", CityAligner.getCityValue("Lakewood"));
        assertEquals("Lakewood", CityAligner.getCityValue("Lakewod"));
        assertEquals("Lakewood", CityAligner.getCityValue("LAKEWOOD"));

        assertEquals("Lamar", CityAligner.getCityValue("Lamar"));
        assertEquals("Lamar", CityAligner.getCityValue("LAMAR"));

        assertEquals("Littleton", CityAligner.getCityValue("Littleton"));
        assertEquals("Littleton", CityAligner.getCityValue("Litleton"));
        assertEquals("Littleton", CityAligner.getCityValue("LITTLETON"));

        assertEquals("Lone Tree", CityAligner.getCityValue("Lone Tree"));
        assertEquals("Lone Tree", CityAligner.getCityValue("LONE TREE"));

        assertEquals("Longmont", CityAligner.getCityValue("Longmont"));
        assertEquals("Longmont", CityAligner.getCityValue("LONGMONT"));

        assertEquals("Louisville", CityAligner.getCityValue("Louisville"));
        assertEquals("Louisville", CityAligner.getCityValue("Louisvile"));
        assertEquals("Louisville", CityAligner.getCityValue("LOUISVILLE"));

        assertEquals("Loveland", CityAligner.getCityValue("Loveland"));
        assertEquals("Loveland", CityAligner.getCityValue("LOVELAND"));

        assertEquals("Montrose", CityAligner.getCityValue("Montrose"));
        assertEquals("Montrose", CityAligner.getCityValue("MONTROSE"));

        assertEquals("Mountain Village", CityAligner.getCityValue("Mountain Village"));
        assertEquals("Mountain Village", CityAligner.getCityValue("MOUNTAIN VILLAGE"));

        assertEquals("Crested Butte", CityAligner.getCityValue("Mt Crested Butte"));
        assertEquals("Crested Butte", CityAligner.getCityValue("MT CRESTED BUTTE"));

        assertEquals("Northglenn", CityAligner.getCityValue("Northglenn"));
        assertEquals("Northglenn", CityAligner.getCityValue("Northglen"));
        assertEquals("Northglenn", CityAligner.getCityValue("NORTHGLENN"));

        assertEquals("Parker", CityAligner.getCityValue("Parker"));
        assertEquals("Parker", CityAligner.getCityValue("PARKER"));

        assertEquals("Pueblo", CityAligner.getCityValue("Pueblo"));
        assertEquals("Pueblo", CityAligner.getCityValue("PUEBLO"));

        assertEquals("Ridgeway", CityAligner.getCityValue("Ridgway"));
        assertEquals("Ridgeway", CityAligner.getCityValue("RIDGWAY"));

        assertEquals("Ridgway", CityAligner.getCityValue("Ridgeway"));
        assertEquals("Ridgway", CityAligner.getCityValue("RIDGEWAY"));

        assertEquals("Rifle", CityAligner.getCityValue("Rifle"));
        assertEquals("Rifle", CityAligner.getCityValue("RIFLE"));

        assertEquals("Sheridan", CityAligner.getCityValue("Sheridan"));
        assertEquals("Sheridan", CityAligner.getCityValue("SHERIDAN"));

        assertEquals("Silverthorne", CityAligner.getCityValue("Silverthorne"));
        assertEquals("Silverthorne", CityAligner.getCityValue("Silverthorn"));
        assertEquals("Silverthorne", CityAligner.getCityValue("Silvertorne"));
        assertEquals("Silverthorne", CityAligner.getCityValue("SILVERTHORNE"));

        assertEquals("Snowmass Village", CityAligner.getCityValue("Snowmass Village"));
        assertEquals("Snowmass Village", CityAligner.getCityValue("Snowmas Village"));
        assertEquals("Snowmass Village", CityAligner.getCityValue("SNOWMASS VILLAGE"));

        assertEquals("Steamboat Springs", CityAligner.getCityValue("Steamboat Springs"));
        assertEquals("Steamboat Springs", CityAligner.getCityValue("Steamboat Spring"));
        assertEquals("Steamboat Springs", CityAligner.getCityValue("STEAMBOAT SPRINGS"));

        assertEquals("Sterling", CityAligner.getCityValue("Sterling"));
        assertEquals("Sterling", CityAligner.getCityValue("STERLING"));

        assertEquals("Telluride", CityAligner.getCityValue("Telluride"));
        assertEquals("Telluride", CityAligner.getCityValue("Teluride"));
        assertEquals("Telluride", CityAligner.getCityValue("TELLURIDE"));

        assertEquals("Thornton", CityAligner.getCityValue("Thornton"));
        assertEquals("Thornton", CityAligner.getCityValue("Thorntn"));
        assertEquals("Thornton", CityAligner.getCityValue("THORNTON"));

        assertEquals("Timnath", CityAligner.getCityValue("Timnath"));
        assertEquals("Timnath", CityAligner.getCityValue("Timath"));
        assertEquals("Timnath", CityAligner.getCityValue("Timnat"));
        assertEquals("Timnath", CityAligner.getCityValue("TIMNATH"));

        assertEquals("Vail", CityAligner.getCityValue("Vail"));
        assertEquals("Vail", CityAligner.getCityValue("VAIL"));

        assertEquals("Westminster", CityAligner.getCityValue("Westminster"));
        assertEquals("Westminster", CityAligner.getCityValue("Westmnster"));
        assertEquals("Westminster", CityAligner.getCityValue("Westminstr"));
        assertEquals("Westminster", CityAligner.getCityValue("WESTMINSTER"));

        assertEquals("Wheat Ridge", CityAligner.getCityValue("Wheat Ridge"));
        assertEquals("Wheat Ridge", CityAligner.getCityValue("Wheat Ridg"));
        assertEquals("Wheat Ridge", CityAligner.getCityValue("Whet Ridge"));
        assertEquals("Wheat Ridge", CityAligner.getCityValue("WHEAT RIDGE"));

        assertEquals("Windsor", CityAligner.getCityValue("Windsor"));
        assertEquals("Windsor", CityAligner.getCityValue("WINDSOR"));

        assertEquals("Winter Park", CityAligner.getCityValue("Winter Park"));
        assertEquals("Winter Park", CityAligner.getCityValue("WINTER PARK"));

        assertEquals("Woodland Park", CityAligner.getCityValue("Woodland Park"));
        assertEquals("Woodland Park", CityAligner.getCityValue("Wodland Park"));
        assertEquals("Woodland Park", CityAligner.getCityValue("WOODLAND PARK"));
    }


    @Test
    void getCityValue_validUpperCase_returnAlignCity() {
        assertEquals("Denver", CityAligner.getCityValue("DENVER"));
        assertEquals("Boulder", CityAligner.getCityValue("BOULDER"));
        assertEquals("Vail", CityAligner.getCityValue("VAIL"));
    }

    @Test
    void getCityValue_misspelledCityName_returnAlignCity() {
        assertEquals("Cherry Hills Village", CityAligner.getCityValue("Cherry Hills Villge"));
        assertEquals("Englewood", CityAligner.getCityValue("Englwood"));
        assertEquals("Broomfield", CityAligner.getCityValue("Broomfild"));
        assertEquals("Timnath", CityAligner.getCityValue("Timnat"));
        assertEquals("Silverthorne", CityAligner.getCityValue("Silvertorne"));
        assertEquals("Mt Crested Butte", CityAligner.getCityValue("Crestd Butte"));
        assertEquals("Ridgeway", CityAligner.getCityValue("Ridgway"));
        assertEquals("Ridgway", CityAligner.getCityValue("RIDGEWAY")); // because it's normalized to "Ridgway"
    }

    @Test
    void getCityValue_nonExistingNames_returnOriginalArgument() {
        assertEquals("Nonexistentville", CityAligner.getCityValue("Nonexistentville"));
        assertEquals("Phoenix", CityAligner.getCityValue("Phoenix"));
        assertEquals("New York", CityAligner.getCityValue("New York"));
    }
}
