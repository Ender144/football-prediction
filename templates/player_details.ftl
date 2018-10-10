<table class="table table-condensed table-hover">
    <#assign contextOpponents=context.opponents>
    <#assign contextPrediction=context.prediction>
    <#assign i=0>
    <thead>
        <tr>
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003"></th>
            <th scope="col" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">Prediction</th>
        </tr>
    </thead>
    <tbody>
        <#list context.season.michiganGamesThisSeason as game>
            <tr <#if i % 2 == 0>class="info"</#if>>
                <th scope="row" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div class="col-lg-2"></div>
                    <div class="col-lg-8 panel" style="margin-bottom: 0; background: #FFCB05; color: #00274C">
                        ${game.us()} - ${game.getOurScore()}
                    </div>
                    <div class="col-lg-2"></div>
                </th>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div class="col-lg-2"></div>
                    <div
                        <#if context.scores.participantIsClosestToUs(contextPrediction, game, game.getOurScore(), game.getTheirScore())>
                            class="col-lg-8 panel" style="margin-bottom: 0; background: #FFCB05; color: #00274C"
                        <#else>class="col-lg-8"
                        </#if>
                    >
                        ${contextPrediction.getGamePrediction(game).ourScore}
                    </div>
                    <div class="col-lg-2"></div>
                </td>
            </tr>
            <tr <#if i % 2 == 0>class="info"</#if>>
                <th scope="row" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div class="col-lg-2"></div>
                    <div class="col-lg-8 panel" style="margin-bottom: 0; background: ${context.colors.getOpponentColors(game, contextOpponents).getLeft()};
                        color: ${context.colors.getOpponentColors(game, contextOpponents).getRight()}">
                        ${game.them()} - ${game.getTheirScore()}
                    </div>
                    <div class="col-lg-2"></div>
                </th>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div class="col-lg-2"></div>
                    <div
                        <#if context.scores.participantIsClosestToThem(contextPrediction, game, game.getOurScore(), game.getTheirScore())>
                            class="col-lg-8 panel" style="margin-bottom: 0; background: ${context.colors.getOpponentColors(game, contextOpponents).getLeft()};
                            color: ${context.colors.getOpponentColors(game, contextOpponents).getRight()}"
                        <#else>class="col-lg-8"
                        </#if>
                     >
                        ${contextPrediction.getGamePrediction(game).theirScore}
                    </div>
                    <div class="col-lg-2"></div>
                </td>
            </tr>
            <tr <#if i % 2 == 0>class="info"</#if>>
                <th scope="row" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div>${game.getActualOutcome()}</div>
                </th>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <span
                        <#if context.scores.predictedCorrectOutcome(context.prediction, game)>
                            class="label label-success"
                        <#elseif game.getActualOutcome().toString() != "Unplayed">
                            class="label label-danger"
                        </#if>>
                        ${context.prediction.getGamePrediction(game).getPredictedOutcome()}
                    </span>
                </td>
            </tr>
            <tr <#if i % 2 == 0>class="info"</#if>>
                <th scope="row" style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    Game Score
                </th>
                <td style="text-align: center; vertical-align: middle; border-top: 1px solid #0003">
                    <div><strong>${context.scores.getScoreForGame(game, contextPrediction.participant)}</strong></div>
                </td>
            </tr>
            <#assign i=i+1>
        </#list>
    </tbody>
</table>