package com.pcistudio.poi.report;

import com.pcistudio.poi.parser.DataField;

public class CarRentalAgentInfo {

    @DataField(name = "Agent")
    private String agent;

    @DataField(name = "Dealer Name")
    private String dealer;

    public String getAgent() {
        return agent;
    }

    public CarRentalAgentInfo setAgent(String agent) {
        this.agent = agent;
        return this;
    }

    public String getDealer() {
        return dealer;
    }

    public CarRentalAgentInfo setDealer(String dealer) {
        this.dealer = dealer;
        return this;
    }
}
