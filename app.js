let gridApi;

const numberSort = (num1, num2) => {
    return num1 - num2;
};

const floatSort = (num1, num2) => {
    return parseFloat(num1) - parseFloat(num2);
};

const gridOptions = {
    autoSizeStrategy: {
        type: 'fitCellContents'
    },
    columnDefs: [
        // Rank,Handle,Codeforces_Handle,Codeforces_Rating,GFG_Handle,GFG_Contest_Score,GFG_Practice_Score,Leetcode_Handle,Leetcode_Rating,Codechef_Handle,Codechef_Rating,HackerRank_Handle,HackerRank_Practice_Score,Percentile
        { headerName: 'Rank', field: 'Rank', sortable: true, width: 70  , comparator: numberSort, lockPosition: true, pinned: 'left'},
        { headerName: 'Handle', field: 'Handle', sortable: true, width: 110, lockPosition: true, pinned: 'left', filter: true },
        { headerName: 'Codeforces Handle', field: 'Codeforces_Handle', sortable: true },
        { headerName: 'Codeforces Rating', field: 'Codeforces_Rating', sortable: true, comparator: numberSort },
        { headerName: 'GFG Handle', field: 'GFG_Handle', sortable: true },
        { headerName: 'GFG Contest Score', field: 'GFG_Contest_Score', sortable: true, comparator: numberSort },
        { headerName: 'GFG Practice Score', field: 'GFG_Practice_Score', sortable: true, comparator: numberSort },
        { headerName: 'Leetcode Handle', field: 'Leetcode_Handle', sortable: true },
        { headerName: 'Leetcode Rating', field: 'Leetcode_Rating', sortable: true, comparator: numberSort },
        { headerName: 'Codechef Handle', field: 'Codechef_Handle', sortable: true },
        { headerName: 'Codechef Rating', field: 'Codechef_Rating', sortable: true, comparator: numberSort },
        { headerName: 'HackerRank Handle', field: 'HackerRank_Handle', sortable: true },
        { headerName: 'HackerRank Practice Score', field: 'HackerRank_Practice_Score', sortable: true, comparator: numberSort },
        { headerName: 'Percentile', field: 'Percentile', sortable: true, width: 120, comparator: floatSort}
    ],

  rowData: [],
};

// XMLHttpRequest in promise format
function makeRequest(method, url, success, error) {
  var httpRequest = new XMLHttpRequest();
  httpRequest.open('GET', url, true);
  httpRequest.responseType = 'arraybuffer';

  httpRequest.open(method, url);
  httpRequest.onload = function () {
    success(httpRequest.response);
  };
  httpRequest.onerror = function () {
    error(httpRequest.response);
  };
  httpRequest.send();
}

// read the raw data and convert it to a XLSX workbook
function convertDataToWorkbook(dataRows) {
  /* convert data to binary string */
  var data = new Uint8Array(dataRows);
  var arr = [];

  for (var i = 0; i !== data.length; ++i) {
    arr[i] = String.fromCharCode(data[i]);
  }

  var bstr = arr.join('');

  return XLSX.read(bstr, { type: 'binary' });
}

// pull out the values we're after, converting it into an array of rowData

function populateGrid(workbook) {
  // our data is in the first sheet
  var firstSheetName = workbook.SheetNames[0];
  var worksheet = workbook.Sheets[firstSheetName];

  // we expect the following columns to be present
  var columns = {
    // Rank,Handle,Codeforces_Handle,Codeforces_Rating,GFG_Handle,GFG_Contest_Score,GFG_Practice_Score,Leetcode_Handle,Leetcode_Rating,Codechef_Handle,Codechef_Rating,HackerRank_Handle,HackerRank_Practice_Score,Percentile
    A: 'Rank',
    B: 'Handle',
    C: 'Codeforces_Handle',
    D: 'Codeforces_Rating',
    E: 'GFG_Handle',
    F: 'GFG_Contest_Score',
    G: 'GFG_Practice_Score',
    H: 'Leetcode_Handle',
    I: 'Leetcode_Rating',
    J: 'Codechef_Handle',
    K: 'Codechef_Rating',
    L: 'HackerRank_Handle',
    M: 'HackerRank_Practice_Score',
    N: 'Percentile',
  };

  var rowData = [];

  // start at the 2nd row - the first row are the headers
  var rowIndex = 2;

  // iterate over the worksheet pulling out the columns we're expecting
  while (worksheet['A' + rowIndex]) {
    var row = {};
    Object.keys(columns).forEach((column) => {
      row[columns[column]] = worksheet[column + rowIndex].w;
    });

    rowData.push(row);

    rowIndex++;
  }

  // finally, set the imported rowData into the grid
  gridApi.setGridOption('rowData', rowData);
}

function importExcel() {
  makeRequest(
    'GET',
    'https://raw.githubusercontent.com/dog-broad/CMRIT2025Leaderboard/main/Leaderboards/CurrentCMRITLeaderboard2025.xlsx',
    // success
    function (data) {
      var workbook = convertDataToWorkbook(data);

      populateGrid(workbook);
    },
    // error
    function (error) {
      throw error;
    }
  );
}

// wait for the document to be loaded, otherwise
// AG Grid will not find the div in the document.
document.addEventListener('DOMContentLoaded', function () {
  // lookup the container we want the Grid to use
  var eGridDiv = document.querySelector('#myGrid');

  // create the grid passing in the div to use together with the columns & data we want to use
  gridApi = agGrid.createGrid(eGridDiv, gridOptions);

  // call importExcel function
  importExcel();
});