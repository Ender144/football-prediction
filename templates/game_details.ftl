<table class="table table-condensed table-sm table-hover">
    <#assign contextOpponents=opponents>
    <#assign contextGame=game>
    <#assign i=0>
    <thead>
        <tr style="font-size: 26px">
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003"></th>
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">Our Score</th>
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">Their Score</th>
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">Outcome</th>
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">Participant Score</th>
        </tr>
    </thead>
    <tbody>
        <#list predictions as prediction>
            <tr <#if i % 2 == 0>class="info"</#if>>
                <th scope="row" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003"><span style="font-size: 48px">${prediction.participant}</span></th>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div class="col-lg-2"></div>
                    <div
                        <#if scores.participantIsClosestToUs(prediction, contextGame)>
                            class="col-lg-8 panel" style="margin-bottom: 0; background: #FFCB05; color: #00274C"
                        <#else>class="col-lg-8"
                        </#if>
                    >
                        <span style="font-size: 48px">${prediction.getGamePrediction(contextGame).ourScore}</span>
                    </div>
                    <div class="col-lg-2"></div>
                </td>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div class="col-lg-2"></div>
                    <div
                        <#if scores.participantIsClosestToThem(prediction, contextGame)>
                            class="col-lg-8 panel" style="margin-bottom: 0; background: ${colors.getOpponentColors(contextGame, contextOpponents).getLeft()};
                            color: ${colors.getOpponentColors(contextGame, contextOpponents).getRight()}"
                            <#else>class="col-lg-8"
                        </#if>
                    >
                        <span style="font-size: 48px">${prediction.getGamePrediction(contextGame).theirScore}</span>
                    </div>
                    <div class="col-lg-2"></div>
                </td>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <span style="font-size: 48px"
                        <#if scores.predictedCorrectOutcome(prediction, contextGame)>
                            class="label label-success"
                        <#elseif contextGame.getActualOutcome().toString() != "Unplayed">
                            class="label label-danger"
                        </#if>>
                        ${prediction.getGamePrediction(contextGame).getPredictedOutcome()}
                    </span>
                </td>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div>
                        <strong>
                            <span style="font-size: 48px">
                                ${scores.getScoreForGame(contextGame, prediction.participant)}
                            </span>
                        </strong>
                    </div>
                </td>
            </tr>
            <#assign i=i+1>
        </#list>
    </tbody>
</table>