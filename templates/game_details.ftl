<table class="table table-condensed table-hover">
    <#assign contextOpponents=context.opponents>
    <#assign contextGame=context.game>
    <#assign i=0>
    <thead>
        <tr>
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003"></th>
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">Our Score</th>
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">Their Score</th>
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">Outcome</th>
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">Participant Score</th>
        </tr>
    </thead>
    <tbody>
        <#list context.predictions as prediction>
            <tr <#if i % 2 == 0>class="info"</#if>>
                <th scope="row" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">${prediction.participant}</th>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div class="col-lg-2"></div>
                    <div
                        <#if context.scores.participantIsClosestToUs(prediction, contextGame)>
                            class="col-lg-8 panel" style="margin-bottom: 0; background: #FFCB05; color: #00274C"
                        <#else>class="col-lg-8"
                        </#if>
                    >
                        ${prediction.getGamePrediction(contextGame).ourScore}
                    </div>
                    <div class="col-lg-2"></div>
                </td>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div class="col-lg-2"></div>
                    <div
                        <#if context.scores.participantIsClosestToThem(prediction, contextGame)>
                            class="col-lg-8 panel" style="margin-bottom: 0; background: ${context.colors.getOpponentColors(contextGame, contextOpponents).getLeft()};
                            color: ${context.colors.getOpponentColors(contextGame, contextOpponents).getRight()}"
                            <#else>class="col-lg-8"
                        </#if>
                    >
                        ${prediction.getGamePrediction(contextGame).theirScore}
                    </div>
                    <div class="col-lg-2"></div>
                </td>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <span
                        <#if context.scores.predictedCorrectOutcome(prediction, contextGame)>
                            class="label label-success"
                        <#elseif contextGame.getActualOutcome().toString() != "Unplayed">
                            class="label label-danger"
                        </#if>>
                        ${prediction.getGamePrediction(contextGame).getPredictedOutcome()}
                    </span>
                </td>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div><strong>${context.scores.getScoreForGame(contextGame, prediction.participant)}</strong></div>
                </td>
            </tr>
            <#assign i=i+1>
        </#list>
    </tbody>
</table>