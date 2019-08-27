<!DOCTYPE html>
<html lang="en">
<#include "./head.ftl"/>
<body>
    <#assign contextGame=game>
    <#assign contextOpponents=opponents>
    <#assign contextOurScore=ourScore>
    <#assign contextTheirScore=theirScore>
    <#include "./navbar.ftl"/>
    <div class="container">
        <h1 style="text-align: center">
            <div class="well"
                style="margin-bottom: 0; background: ${colors.getOpponentColors(contextGame, contextOpponents).getLeft()};
                 color: ${colors.getOpponentColors(contextGame, contextOpponents).getRight()}">
                <#if contextGame.getAwayTeam() == contextGame.us()>
                    ${contextGame.us()} AT ${contextGame.them()}
                <#else>
                    ${contextGame.us()} VS ${contextGame.them()}
                </#if>
                Score: ${contextOurScore} - ${contextTheirScore}
                <span style="vertical-align: middle"
                    <#if contextGame.getActualOutcome().toString() == "W">
                        class="label label-success">
                    <#elseif contextGame.getActualOutcome().toString() != "Unplayed">
                        class="label label-danger">
                    <#else>
                        class="label">
                    </#if>
                    (${contextGame.getActualOutcome()})
                </span>
            </div>
        </h1>
        <#include "./game_details.ftl"/>
    </div>
</body>
</html>