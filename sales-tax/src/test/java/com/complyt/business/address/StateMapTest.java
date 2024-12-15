package com.complyt.business.address;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

public class StateMapTest {


    @Test
    public void testStatesToStandartizedState() {
        Map<String, String> statesMap = StateMap.statesToStandartizedState;

        // Test Alabama
        Assertions.assertEquals("Alabama", statesMap.get("AL"));
        Assertions.assertEquals("Alabama", statesMap.get("ALABAMA"));
        Assertions.assertEquals("Alabama", statesMap.get("ALABAMMA"));
        Assertions.assertEquals("Alabama", statesMap.get("ALABMA"));

        // Test Alaska
        Assertions.assertEquals("Alaska", statesMap.get("AK"));
        Assertions.assertEquals("Alaska", statesMap.get("ALASKA"));
        Assertions.assertEquals("Alaska", statesMap.get("ALASCA"));
        Assertions.assertEquals("Alaska", statesMap.get("ALASKAA"));

        // Test Arizona
        Assertions.assertEquals("Arizona", statesMap.get("AZ"));
        Assertions.assertEquals("Arizona", statesMap.get("ARIZONA"));
        Assertions.assertEquals("Arizona", statesMap.get("ARIZONNA"));

        // Test Arkansas
        Assertions.assertEquals("Arkansas", statesMap.get("AR"));
        Assertions.assertEquals("Arkansas", statesMap.get("ARKANSAS"));
        Assertions.assertEquals("Arkansas", statesMap.get("ARKANSA"));
        Assertions.assertEquals("Arkansas", statesMap.get("ARKANSS"));

        // Test California
        Assertions.assertEquals("California", statesMap.get("CA"));
        Assertions.assertEquals("California", statesMap.get("CALIFORNIA"));
        Assertions.assertEquals("California", statesMap.get("CALIFONIA"));
        Assertions.assertEquals("California", statesMap.get("CALIFORNIAA"));

        // Test Colorado
        Assertions.assertEquals("Colorado", statesMap.get("CO"));
        Assertions.assertEquals("Colorado", statesMap.get("COLORADO"));
        Assertions.assertEquals("Colorado", statesMap.get("COLORODO"));
        Assertions.assertEquals("Colorado", statesMap.get("COLORDAO"));

        // Test Connecticut
        Assertions.assertEquals("Connecticut", statesMap.get("CT"));
        Assertions.assertEquals("Connecticut", statesMap.get("CONNECTICUT"));
        Assertions.assertEquals("Connecticut", statesMap.get("CONNECTICUTT"));
        Assertions.assertEquals("Connecticut", statesMap.get("CONNETICUT"));

        // Test Delaware
        Assertions.assertEquals("Delaware", statesMap.get("DE"));
        Assertions.assertEquals("Delaware", statesMap.get("DELAWARE"));
        Assertions.assertEquals("Delaware", statesMap.get("DELLAWARE"));
        Assertions.assertEquals("Delaware", statesMap.get("DELAWERE"));

        // Test District of Columbia
        Assertions.assertEquals("District of Columbia", statesMap.get("DC"));
        Assertions.assertEquals("District of Columbia", statesMap.get("DISTRICT OF COLUMBIA"));
        Assertions.assertEquals("District of Columbia", statesMap.get("DISTRICT  OF  COLUMBIA"));
        Assertions.assertEquals("District of Columbia", statesMap.get("DISTRICT  OF COLUMBIA"));
        Assertions.assertEquals("District of Columbia", statesMap.get("DISTRICT OF  COLUMBIA"));
        Assertions.assertEquals("District of Columbia", statesMap.get("DISTRICT OF COLUMBA"));

        // Test Florida
        Assertions.assertEquals("Florida", statesMap.get("FL"));
        Assertions.assertEquals("Florida", statesMap.get("FLORIDA"));
        Assertions.assertEquals("Florida", statesMap.get("FLORIDIA"));
        Assertions.assertEquals("Florida", statesMap.get("FLORDIA"));

        // Test Georgia
        Assertions.assertEquals("Georgia", statesMap.get("GA"));
        Assertions.assertEquals("Georgia", statesMap.get("GEORGIA"));
        Assertions.assertEquals("Georgia", statesMap.get("GEORIGA"));
        Assertions.assertEquals("Georgia", statesMap.get("GEORGIAA"));

        // Test Hawaii
        Assertions.assertEquals("Hawaii", statesMap.get("HI"));
        Assertions.assertEquals("Hawaii", statesMap.get("HAWAII"));
        Assertions.assertEquals("Hawaii", statesMap.get("HAWAI"));
        Assertions.assertEquals("Hawaii", statesMap.get("HAWI"));

        // Test Idaho
        Assertions.assertEquals("Idaho", statesMap.get("ID"));
        Assertions.assertEquals("Idaho", statesMap.get("IDAHO"));
        Assertions.assertEquals("Idaho", statesMap.get("IDAHOE"));
        Assertions.assertEquals("Idaho", statesMap.get("IDAAHO"));

        // Test Illinois
        Assertions.assertEquals("Illinois", statesMap.get("IL"));
        Assertions.assertEquals("Illinois", statesMap.get("ILLINOIS"));
        Assertions.assertEquals("Illinois", statesMap.get("ILLINIOS"));
        Assertions.assertEquals("Illinois", statesMap.get("ILLINOISS"));

        // Test Indiana
        Assertions.assertEquals("Indiana", statesMap.get("IN"));
        Assertions.assertEquals("Indiana", statesMap.get("INDIANA"));
        Assertions.assertEquals("Indiana", statesMap.get("INDINNA"));
        Assertions.assertEquals("Indiana", statesMap.get("INDIENA"));

        // Test Iowa
        Assertions.assertEquals("Iowa", statesMap.get("IA"));
        Assertions.assertEquals("Iowa", statesMap.get("IOWA"));
        Assertions.assertEquals("Iowa", statesMap.get("IOAW"));
        Assertions.assertEquals("Iowa", statesMap.get("IWA"));

        // Test Kansas
        Assertions.assertEquals("Kansas", statesMap.get("KS"));
        Assertions.assertEquals("Kansas", statesMap.get("KANSAS"));
        Assertions.assertEquals("Kansas", statesMap.get("KANAS"));
        Assertions.assertEquals("Kansas", statesMap.get("KANSASS"));

        // Test Kentucky
        Assertions.assertEquals("Kentucky", statesMap.get("KY"));
        Assertions.assertEquals("Kentucky", statesMap.get("KENTUCKY"));
        Assertions.assertEquals("Kentucky", statesMap.get("KENTUKY"));
        Assertions.assertEquals("Kentucky", statesMap.get("KENTUCKKY"));

        // Test Louisiana
        Assertions.assertEquals("Louisiana", statesMap.get("LA"));
        Assertions.assertEquals("Louisiana", statesMap.get("LOUISIANA"));
        Assertions.assertEquals("Louisiana", statesMap.get("LOUISANA"));
        Assertions.assertEquals("Louisiana", statesMap.get("LOUISIANNA"));

        // Test Massachusetts
        Assertions.assertEquals("Massachusetts", statesMap.get("MA"));
        Assertions.assertEquals("Massachusetts", statesMap.get("MASSACHUSETTS"));
        Assertions.assertEquals("Massachusetts", statesMap.get("MASSACHUSSETS"));
        Assertions.assertEquals("Massachusetts", statesMap.get("MASSACHUSSETTS"));

        // Test Maine
        Assertions.assertEquals("Maine", statesMap.get("ME"));
        Assertions.assertEquals("Maine", statesMap.get("MAINE"));
        Assertions.assertEquals("Maine", statesMap.get("MAIINE"));
        Assertions.assertEquals("Maine", statesMap.get("MAIN"));

        // Test Maryland
        Assertions.assertEquals("Maryland", statesMap.get("MD"));
        Assertions.assertEquals("Maryland", statesMap.get("MARYLAND"));
        Assertions.assertEquals("Maryland", statesMap.get("MARYLANDD"));
        Assertions.assertEquals("Maryland", statesMap.get("MARYLANNDD"));

        // Test Michigan
        Assertions.assertEquals("Michigan", statesMap.get("MI"));
        Assertions.assertEquals("Michigan", statesMap.get("MICHIGAN"));
        Assertions.assertEquals("Michigan", statesMap.get("MICHIGON"));
        Assertions.assertEquals("Michigan", statesMap.get("MICHAGAN"));

        // Test Minnesota
        Assertions.assertEquals("Minnesota", statesMap.get("MN"));
        Assertions.assertEquals("Minnesota", statesMap.get("MINNESOTA"));
        Assertions.assertEquals("Minnesota", statesMap.get("MINESOTA"));
        Assertions.assertEquals("Minnesota", statesMap.get("MINNESOTTA"));

        // Test Mississippi
        Assertions.assertEquals("Mississippi", statesMap.get("MS"));
        Assertions.assertEquals("Mississippi", statesMap.get("MISSISSIPPI"));
        Assertions.assertEquals("Mississippi", statesMap.get("MISSISIPPI"));
        Assertions.assertEquals("Mississippi", statesMap.get("MISSSISSIPPI"));

        // Test Missouri
        Assertions.assertEquals("Missouri", statesMap.get("MO"));
        Assertions.assertEquals("Missouri", statesMap.get("MISSOURI"));
        Assertions.assertEquals("Missouri", statesMap.get("MISOURI"));
        Assertions.assertEquals("Missouri", statesMap.get("MISSOURRI"));

        // Test Montana
        Assertions.assertEquals("Montana", statesMap.get("MT"));
        Assertions.assertEquals("Montana", statesMap.get("MONTANA"));
        Assertions.assertEquals("Montana", statesMap.get("MONTANNA"));
        Assertions.assertEquals("Montana", statesMap.get("MONTENA"));

        // Test Nebraska
        Assertions.assertEquals("Nebraska", statesMap.get("NE"));
        Assertions.assertEquals("Nebraska", statesMap.get("NEBRASKA"));
        Assertions.assertEquals("Nebraska", statesMap.get("NEBRASCA"));
        Assertions.assertEquals("Nebraska", statesMap.get("NEBRAKA"));

        // Test Nevada
        Assertions.assertEquals("Nevada", statesMap.get("NV"));
        Assertions.assertEquals("Nevada", statesMap.get("NEVADA"));
        Assertions.assertEquals("Nevada", statesMap.get("NEVVADA"));
        Assertions.assertEquals("Nevada", statesMap.get("NEVADAA"));

        // Test New Hampshire
        Assertions.assertEquals("New Hampshire", statesMap.get("NH"));
        Assertions.assertEquals("New Hampshire", statesMap.get("NEW HAMPSHIRE"));
        Assertions.assertEquals("New Hampshire", statesMap.get("NEW HAMPSIRE"));
        Assertions.assertEquals("New Hampshire", statesMap.get("NEW  HAMPSHIRE"));

        // Test New Jersey
        Assertions.assertEquals("New Jersey", statesMap.get("NJ"));
        Assertions.assertEquals("New Jersey", statesMap.get("NEW JERSEY"));
        Assertions.assertEquals("New Jersey", statesMap.get("NEW JERSY"));
        Assertions.assertEquals("New Jersey", statesMap.get("NEW JERZEY"));

        // Test New Mexico
        Assertions.assertEquals("New Mexico", statesMap.get("NM"));
        Assertions.assertEquals("New Mexico", statesMap.get("NEW MEXICO"));
        Assertions.assertEquals("New Mexico", statesMap.get("NEW  MEXICO"));
        Assertions.assertEquals("New Mexico", statesMap.get("NEW MEXICOO"));

        // Test New York
        Assertions.assertEquals("New York", statesMap.get("NY"));
        Assertions.assertEquals("New York", statesMap.get("NEW YORK"));
        Assertions.assertEquals("New York", statesMap.get("NEW  YORK"));
        Assertions.assertEquals("New York", statesMap.get("NEW YOKR"));
        Assertions.assertEquals("New York", statesMap.get("NEW YOURK"));

        // Test North Carolina
        Assertions.assertEquals("North Carolina", statesMap.get("NC"));
        Assertions.assertEquals("North Carolina", statesMap.get("NORTH CAROLINA"));
        Assertions.assertEquals("North Carolina", statesMap.get("NORTH  CAROLINA"));
        Assertions.assertEquals("North Carolina", statesMap.get("NORTH CAROLNA"));

        // Test North Dakota
        Assertions.assertEquals("North Dakota", statesMap.get("ND"));
        Assertions.assertEquals("North Dakota", statesMap.get("NORTH DAKOTA"));
        Assertions.assertEquals("North Dakota", statesMap.get("NORTH  DAKOTA"));
        Assertions.assertEquals("North Dakota", statesMap.get("NORTH DAKOA"));

        // Test Ohio
        Assertions.assertEquals("Ohio", statesMap.get("OH"));
        Assertions.assertEquals("Ohio", statesMap.get("OHIO"));
        Assertions.assertEquals("Ohio", statesMap.get("OHHIO"));

        // Test Oklahoma
        Assertions.assertEquals("Oklahoma", statesMap.get("OK"));
        Assertions.assertEquals("Oklahoma", statesMap.get("OKLAHOMA"));
        Assertions.assertEquals("Oklahoma", statesMap.get("OKLOHOMA"));
        Assertions.assertEquals("Oklahoma", statesMap.get("OKLAHOMAA"));

        // Test Oregon
        Assertions.assertEquals("Oregon", statesMap.get("OR"));
        Assertions.assertEquals("Oregon", statesMap.get("OREGON"));
        Assertions.assertEquals("Oregon", statesMap.get("OREGONN"));
        Assertions.assertEquals("Oregon", statesMap.get("ORAGON"));

        // Test Pennsylvania
        Assertions.assertEquals("Pennsylvania", statesMap.get("PA"));
        Assertions.assertEquals("Pennsylvania", statesMap.get("PENNSYLVANIA"));
        Assertions.assertEquals("Pennsylvania", statesMap.get("PENNSLYVANIA"));
        Assertions.assertEquals("Pennsylvania", statesMap.get("PENNSYLVANNIA"));

        // Test Rhode Island
        Assertions.assertEquals("Rhode Island", statesMap.get("RI"));
        Assertions.assertEquals("Rhode Island", statesMap.get("RHODE ISLAND"));
        Assertions.assertEquals("Rhode Island", statesMap.get("RODE ISLAND"));
        Assertions.assertEquals("Rhode Island", statesMap.get("RHODE  ISLAND"));

        // Test South Carolina
        Assertions.assertEquals("South Carolina", statesMap.get("SC"));
        Assertions.assertEquals("South Carolina", statesMap.get("SOUTH CAROLINA"));
        Assertions.assertEquals("South Carolina", statesMap.get("SOUTH  CAROLINA"));
        Assertions.assertEquals("South Carolina", statesMap.get("SOUTH CAROLLINA"));

        // Test South Dakota
        Assertions.assertEquals("South Dakota", statesMap.get("SD"));
        Assertions.assertEquals("South Dakota", statesMap.get("SOUTH DAKOTA"));
        Assertions.assertEquals("South Dakota", statesMap.get("SOUTH  DAKOTA"));
        Assertions.assertEquals("South Dakota", statesMap.get("SOUTH DAKOTAA"));

        // Test Tennessee
        Assertions.assertEquals("Tennessee", statesMap.get("TN"));
        Assertions.assertEquals("Tennessee", statesMap.get("TENNESSEE"));
        Assertions.assertEquals("Tennessee", statesMap.get("TENNESEE"));
        Assertions.assertEquals("Tennessee", statesMap.get("TENNESSE"));

        // Test Texas
        Assertions.assertEquals("Texas", statesMap.get("TX"));
        Assertions.assertEquals("Texas", statesMap.get("TEXAS"));
        Assertions.assertEquals("Texas", statesMap.get("TEXASS"));
        Assertions.assertEquals("Texas", statesMap.get("TEXAAS"));

        // Test Utah
        Assertions.assertEquals("Utah", statesMap.get("UT"));
        Assertions.assertEquals("Utah", statesMap.get("UTAH"));
        Assertions.assertEquals("Utah", statesMap.get("UTAHH"));
        Assertions.assertEquals("Utah", statesMap.get("UTHA"));

        // Test Vermont
        Assertions.assertEquals("Vermont", statesMap.get("VT"));
        Assertions.assertEquals("Vermont", statesMap.get("VERMONT"));
        Assertions.assertEquals("Vermont", statesMap.get("VERMOT"));
        Assertions.assertEquals("Vermont", statesMap.get("VERMONTT"));

        // Test Virginia
        Assertions.assertEquals("Virginia", statesMap.get("VA"));
        Assertions.assertEquals("Virginia", statesMap.get("VIRGINIA"));
        Assertions.assertEquals("Virginia", statesMap.get("VIRGINNIA"));
        Assertions.assertEquals("Virginia", statesMap.get("VIGINIA "));

        // Test Washington
        Assertions.assertEquals("Washington", statesMap.get("WA"));
        Assertions.assertEquals("Washington", statesMap.get("WASHINGTON"));
        Assertions.assertEquals("Washington", statesMap.get("WASHINTON"));
        Assertions.assertEquals("Washington", statesMap.get("WASHINGTONN"));
        Assertions.assertEquals("District of Columbia", statesMap.get("WASHINGTON DC"));
        Assertions.assertEquals("District of Columbia", statesMap.get("WASHINGTON D.C"));
        Assertions.assertEquals("District of Columbia", statesMap.get("WASHINGTON D.C."));

        // Test West Virginia
        Assertions.assertEquals("West Virginia", statesMap.get("WV"));
        Assertions.assertEquals("West Virginia", statesMap.get("WEST VIRGINIA"));
        Assertions.assertEquals("West Virginia", statesMap.get("WEST  VIRGINIA"));
        Assertions.assertEquals("West Virginia", statesMap.get("WEST VIRGINNIA"));

        // Test Wisconsin
        Assertions.assertEquals("Wisconsin", statesMap.get("WI"));
        Assertions.assertEquals("Wisconsin", statesMap.get("WISCONSIN"));
        Assertions.assertEquals("Wisconsin", statesMap.get("WISCONNSIN"));
        Assertions.assertEquals("Wisconsin", statesMap.get("WISCONSIN  "));

        // Test Wyoming
        Assertions.assertEquals("Wyoming", statesMap.get("WY"));
        Assertions.assertEquals("Wyoming", statesMap.get("WYOMING"));
        Assertions.assertEquals("Wyoming", statesMap.get("WYOOMING"));
        Assertions.assertEquals("Wyoming", statesMap.get("WIOMING"));
    }
}