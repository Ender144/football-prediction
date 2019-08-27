<div class="table-area">
    <table class="table table-condensed table-hover responsive-table">
        <#assign contextOpponents=opponents>
        <#assign i=0>
        <thead>
            <tr>
                <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003"></th>
                <#list predictions as prediction>
                    <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                        <h5>
                            <a href="/${prediction.participant}">
                                ${prediction.participant} (${scores.getCurrentParticipantScore(prediction.participant)})
                            </a>
                        </h5>
                    </th>
                </#list>
            </tr>
        </thead>
        <tbody>
            <#list season.michiganGamesThisSeason as game>
                <tr <#if i % 2 == 0>class="info"</#if>>
                    <th scope="row" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                        <div class="panel" style="margin-bottom: 0; background: #FFCB05; color: #00274C">
                            ${game.us()} - ${game.getOurScore()}
                        </div>
                    </th>
                    <#list predictions as prediction>
                        <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                            <div
                                <#if scores.participantIsClosestToUs(prediction, game)>
                                    class="panel" style="margin-bottom: 0; background: #FFCB05; color: #00274C"
                                </#if>>
                                ${prediction.getGamePrediction(game).ourScore}
                            </div>
                        </td>
                    </#list>
                </tr>
                <tr <#if i % 2 == 0>class="info"</#if>>
                    <th scope="row" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                        <div class="panel" style="margin-bottom: 0; background: ${colors.getOpponentColors(game, contextOpponents).getLeft()};
                            color: ${colors.getOpponentColors(game, contextOpponents).getRight()}">
                            ${game.them()} - ${game.getTheirScore()}
                        </div>
                    </th>
                    <#list predictions as prediction>
                        <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                            <div
                                <#if scores.participantIsClosestToThem(prediction, game)>
                                    class="panel" style="margin-bottom:0; background: ${colors.getOpponentColors(game, contextOpponents).getLeft()};
                                    color: ${colors.getOpponentColors(game, contextOpponents).getRight()}"
                                 </#if>
                            >
                                ${prediction.getGamePrediction(game).theirScore}
                            </div>
                        </td>
                    </#list>
                </tr>
                <tr <#if i % 2 == 0>class="info"</#if>>
                    <th scope="row" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                        <div>${game.getActualOutcome()}</div>
                    </th>
                    <#list predictions as prediction>
                        <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                            <span
                                <#if scores.predictedCorrectOutcome(prediction, game)>
                                    class="label label-success"
                                <#elseif game.getActualOutcome().toString() != "Unplayed">
                                    class="label label-danger"
                                </#if>>
                                ${prediction.getGamePrediction(game).getPredictedOutcome()}
                            </span>
                        </td>
                    </#list>
                </tr>
                <tr <#if i % 2 == 0>class="info"</#if>>
                    <th scope="row" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                        Game Score
                    </th>
                    <#list predictions as prediction>
                        <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                            <div><strong>${scores.getScoreForGame(game, prediction.participant)}</strong></div>
                        </td>
                    </#list>
                </tr>
                <#assign i=i+1>
            </#list>
        </tbody>
    </table>
</div>