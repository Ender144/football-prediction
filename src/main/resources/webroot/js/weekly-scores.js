var Vis = function() {
    var vis = {};

    var weeklyScoresCanvas = document.getElementById("weekly-scores").getContext('2d');

    /**
     * This method is used to generate tree views by leveraging bootstrap-treeview.js. It finds all divs with class
     * "workbench-tree" and generates content for the layout by retrieving the tree structure from a specified remote URL.
     * This method makes use of several data-attributes on the div with the "workbench-tree" class:
     *
     * remote: The remote URL to retrieve data from. The URL should return a JSON tree structure under the root key "tree".
     * input-selector: (optional) A jQuery selector used to identify an input DOM element which can store a node attribute
     *                    on the currently-selected node. The attribute can be defined as needed; by default it uses the node's
     *                    "nodeid" attribute.
     * node-attribute: (optional) Used with the input-selector data-attribute, this defines the attribute of the currently-
     *                    selected node which will be stored in the input DOM element. Default: "nodeid"
     */
    vis.registerLineGraphs = function() {
        var $graphs = $("div.graph");
        $graphs.each(function(index) {
            var $this = $(this);
            var remote = $this.data("remote");

            $.ajax(remote, {
                dataType: 'json',
                method: 'GET',
                data: '',
                success: function(data, textStatus, jqXHR) {
                    var scores = data["scores"];
                    var myLineChart = new Chart(weeklyScoresCanvas, {
                        type: 'line',
                        data: {
                            labels: [scores["games"]],
                            datasets: [{

                                    label: "My First dataset",
                                    data: [65, 59, 80, 81, 56, 55, 40],
                                    backgroundColor: [
                                        'rgba(105, 0, 132, .2)',
                                    ],
                                    borderColor: [
                                        'rgba(200, 99, 132, .7)',
                                    ],
                                    borderWidth: 2
                                },
                                {
                                    label: "My Second dataset",
                                    data: [28, 48, 40, 19, 86, 27, 90],
                                    backgroundColor: [
                                        'rgba(0, 137, 132, .2)',
                                    ],
                                    borderColor: [
                                        'rgba(0, 10, 130, .7)',
                                    ],
                                    borderWidth: 2
                                }
                            ]
                        },
                        options: {
                            responsive: true
                        }
                    });
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    var responseObj = jQuery.parseJSON(jqXHR.responseText);
                }
            });
        });
    };

    return workbench;
}();

$(document).ready(function() {
    Workbench.registerLineGraphs();
});