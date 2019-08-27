<!DOCTYPE html>
<html lang="en">
<#include "./head.ftl"/>
<body>
    <#assign contextPrediction=prediction>
    <#include "./navbar.ftl"/>
    <div class="container">
        <h1 style="text-align: center">
            ${contextPrediction.participant} - Score: ${scores.getCurrentParticipantScore(contextPrediction.participant)}
        </h1>
        <#include "./player_details.ftl"/>
    </div>
</body>
</html>