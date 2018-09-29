<nav class="navbar navbar-inverse">
    <div class="container" style="height: 50px">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        <a class="navbar-brand" href="/">Home</a>
    </div>
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
        <ul class="nav navbar-nav">
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                    People <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                    <#list context.predictions as prediction>
                        <li><a href="/${prediction.participant}">${prediction.participant}</a></li>
                    </#list>
                </ul>
            </li>
        </ul>
        <ul class="nav navbar-nav">
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                    Games <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                    <#list context.season.michiganGamesThisSeason as game>
                        <li><a href="/${game.them()}">${game.them()}</a></li>
                    </#list>
                </ul>
            </li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="/rebuildSeason/">Pull Scores<span class="sr-only">(current)</span></a></li>
        </ul>
    </div>
</nav>