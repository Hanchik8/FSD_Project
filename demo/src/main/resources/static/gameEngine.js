let board;
let turn;

let pickedFig = null;
let pickedPos = null;

let whiteKingPos;
let blackKingPos;

let litCells = [];

function startGame() {
  const startSetup = [
    ["R", "N", "B", "Q", "K", "B", "N", "R"],
    ["P", "P", "P", "P", "P", "P", "P", "P"],
    [".", ".", ".", ".", ".", ".", ".", "."],
    [".", ".", ".", ".", ".", ".", ".", "."],
    [".", ".", ".", ".", ".", ".", ".", "."],
    [".", ".", ".", ".", ".", ".", ".", "."],
    ["p", "p", "p", "p", "p", "p", "p", "p"],
    ["r", "n", "b", "q", "k", "b", "n", "r"],
  ];

  const startTurn = "white";
  
  whiteKingPos = [7, 4];
  blackKingPos = [0, 4];

  setBoard(startSetup, startTurn);
}

function setBoard(setup, player) {
  board = setup;
  turn = player;

  for (let i = 0; i < 8; i++) {
    for (let j = 0; j < 8; j++) {
      if (setup[i][j] != ".") {
        putFig(setup[i][j], [i + 1, j + 1]);
      }
    }
  }
}

function putFig(fig, pos) {
  const cell = document.getElementById(`${pos[0]}${pos[1]}`);
  const img = document.createElement("img");
  img.classList.add("piece");
  img.id = fig;
  img.src = getPic(fig);
  cell.appendChild(img);
}

function getPic(fig) {
  switch (fig) {
    case "R":
      return "assets/black_rook.png";
    case "N":
      return "assets/black_knight.png";
    case "B":
      return "assets/black_bishop.png";
    case "Q":
      return "assets/black_queen.png";
    case "K":
      return "assets/black_king.png";
    case "P":
      return "assets/black_pawn.png";
    case "r":
      return "assets/white_rook.png";
    case "n":
      return "assets/white_knight.png";
    case "b":
      return "assets/white_bishop.png";
    case "q":
      return "assets/white_queen.png";
    case "k":
      return "assets/white_king.png";
    case "p":
      return "assets/white_pawn.png";
  }
}

function setClicks() {
  const cells = document.getElementsByClassName("square");
  for (const cell of cells) {
    cell.addEventListener("click", clickCell);
  }
}

function clickCell(event) {
  const cell = event.currentTarget;
  const nums = cell.id.split("").map(Number);
  const cellPos = [nums[0] - 1, nums[1] - 1];
  
  if (pickedFig) {
    if (pickedPos[0] === cellPos[0] && pickedPos[1] === cellPos[1]) {
      dropFig();
      return;
    }
    if (checkMove(pickedPos, cellPos)) {
      moveFig(pickedFig, pickedPos, cellPos);
    }
    dropFig();
    return;
  }
  
  if (cell.children.length > 0) {
    const fig = cell.children[0];
    const figId = fig.id;
    const figIsWhite = figId === figId.toLowerCase();
    const turnIsWhite = turn === "white";
    if (figIsWhite === turnIsWhite) {
      pickFig(fig, cellPos);
    }
  }
}

function pickFig(fig, pos) {
  pickedFig = fig;
  pickedPos = pos;
  
  const cell = document.getElementById(`${pos[0] + 1}${pos[1] + 1}`);
  cell.classList.add("selected-piece");
  showMoves(pos);
}

function dropFig() {
  if (pickedFig) {
    const cell = document.getElementById(`${pickedPos[0] + 1}${pickedPos[1] + 1}`);
    cell.classList.remove("selected-piece");
    clearLights();
    pickedFig = null;
    pickedPos = null;
  }
}

function moveFig(fig, startPos, endPos) {
  const figOnBoard = board[startPos[0]][startPos[1]];

  if (figOnBoard != ".") {
    if (
      (figOnBoard === figOnBoard.toUpperCase() && turn == "black") ||
      (figOnBoard === figOnBoard.toLowerCase() && turn == "white")
    ) {
      if (figOnBoard === 'k') {
        whiteKingPos = [endPos[0], endPos[1]];
      } else if (figOnBoard === 'K') {
        blackKingPos = [endPos[0], endPos[1]];
      }

      const oldBoard = JSON.parse(JSON.stringify(board));
      board[startPos[0]][startPos[1]] = ".";
      board[endPos[0]][endPos[1]] = figOnBoard;
      
      const kingPos = turn === "white" ? whiteKingPos : blackKingPos;
      if (kingInCheck(kingPos, turn)) {
        board = oldBoard;
        return false;
      }

      const endCell = document.getElementById(`${endPos[0] + 1}${endPos[1] + 1}`);
      endCell.textContent = "";
      endCell.appendChild(fig);

      const nextTurn = turn === "white" ? "black" : "white";
      const nextKingPos = nextTurn === "white" ? whiteKingPos : blackKingPos;
      
      if (kingInCheck(nextKingPos, nextTurn)) {
        showCheckMsg(nextTurn);
        lightKing(nextTurn);
        if (checkMate(nextTurn)) {
          showMateMsg(nextTurn);
        }
      }

      turn = nextTurn;
      return true;
    }
  }
  return false;
}

function checkMove(startPos, endPos, flag = false) {
  const fig = board[startPos[0]][startPos[1]];

  if (!flag && isKingAt(endPos)) {
    return false;
  }

  switch (fig) {
    case "r":
    case "R":
      return checkRook(startPos, endPos);
    case "n":
    case "N":
      return checkKnight(startPos, endPos);
    case "b":
    case "B":
      return checkBishop(startPos, endPos);
    case "q":
    case "Q":
      return checkQueen(startPos, endPos);
    case "k":
    case "K":
      return checkKing(startPos, endPos);
    case "p":
      return checkPawn("white", startPos, endPos);
    case "P":
      return checkPawn("black", startPos, endPos);
  }
}

function checkBishop(startPos, endPos) {
  if (
    endPos[0] - endPos[1] === startPos[0] - startPos[1] ||
    endPos[0] + endPos[1] === startPos[0] + startPos[1]
  ) {
    if (!checkPath(startPos, endPos)) return false;
    return true;
  } else {
    return false;
  }
}

function checkRook(startPos, endPos) {
  if (
    endPos[0] === startPos[0] ||
    endPos[1] === startPos[1]
  ) {
    if (!checkPath(startPos, endPos)) return false;
    return true;
  } else {
    return false;
  }
}

function checkKing(startPos, endPos) {
  if (
    [-1, 0, 1].includes(endPos[0] - startPos[0]) &&
    [-1, 0, 1].includes(endPos[1] - startPos[1])
  ) {
    if (isSameColor(endPos)) return false;
    return true;
  } else {
    return false;
  }
}

function checkQueen(startPos, endPos) {
  if (
    endPos[0] - endPos[1] === startPos[0] - startPos[1] ||
    endPos[0] + endPos[1] === startPos[0] + startPos[1] ||
    endPos[0] === startPos[0] ||
    endPos[1] === startPos[1]
  ) {
    if (!checkPath(startPos, endPos)) return false;
    return true;
  } else {
    return false;
  }
}

function checkPawn(color, startPos, endPos) {
  let step = color === "black" ? 1 : -1;
  let canTake = false;

  if (
    endPos[0] === startPos[0] + step &&
    [startPos[1] - 1, startPos[1] + 1].includes(endPos[1])
  ) {
    if (isOpp(endPos)) {
      canTake = true;
    }
  }

  let firstStep = false;
  if (
    (color === "white" && startPos[0] === 6) ||
    (color === "black" && startPos[0] === 1)
  ) {
    firstStep = true;
  }

  if (
    ((endPos[0] === startPos[0] + step ||
      (endPos[0] === startPos[0] + step * 2 && firstStep)) &&
      endPos[1] === startPos[1]) ||
    canTake
  ) {
    if (isSameColor(endPos)) return false;
    else if (!canTake && isOpp(endPos)) return false;
    return true;
  } else {
    return false;
  }
}

function checkKnight(startPos, endPos) {
  if (
    ([-2, 2].includes(endPos[0] - startPos[0]) &&
     [-1, 1].includes(endPos[1] - startPos[1])) ||
    ([-2, 2].includes(endPos[1] - startPos[1]) &&
     [-1, 1].includes(endPos[0] - startPos[0]))
  ) {
    if (isSameColor(endPos)) return false;
    return true;
  } else {
    return false;
  }
}

function checkPath(startPos, endPos) {
  const xDiff = endPos[0] - startPos[0];
  const yDiff = endPos[1] - startPos[1];

  let xStep = 0;
  let yStep = 0;

  if (xDiff < 0) xStep = -1;
  else if (xDiff > 0) xStep = 1;

  if (yDiff < 0) yStep = -1;
  else if (yDiff > 0) yStep = 1;

  let curX = startPos[0] + xStep;
  let curY = startPos[1] + yStep;

  while (curX != endPos[0] || curY != endPos[1]) {
    if (board[curX][curY] !== ".") return false;
    curX += xStep;
    curY += yStep;
  }

  if (isSameColor(endPos)) return false;
  return true;
}

function isSameColor(pos) {
  const fig = board[pos[0]][pos[1]];
  if (fig !== ".") {
    const figWhite = fig === fig.toLowerCase();
    const turnWhite = turn === "white";
    return figWhite === turnWhite;
  }
  return false;
}

function isOpp(pos) {
  const fig = board[pos[0]][pos[1]];
  if (fig !== ".") {
    const figWhite = fig === fig.toLowerCase();
    const turnWhite = turn === "white";
    return figWhite !== turnWhite;
  }
  return false;
}

function kingInCheck(kPos, player) {
  for (let i = 0; i < 8; i++) {
    for (let j = 0; j < 8; j++) {
      const fig = board[i][j];
      if (fig === ".") continue;
      const figWhite = fig === fig.toLowerCase();
      const playerWhite = player === "white";
      if (figWhite === playerWhite) continue;
      if (checkMove([i, j], kPos, true)) return true;
    }
  }
  return false;
}

function isKingAt(pos) {
  const fig = board[pos[0]][pos[1]];
  return fig === 'k' || fig === 'K';
}

function checkMate(player) {
  const kPos = player === "white" ? whiteKingPos : blackKingPos;
  if (!kingInCheck(kPos, player)) return false;
  
  for (let i = 0; i < 8; i++) {
    for (let j = 0; j < 8; j++) {
      const fig = board[i][j];
      if (fig === ".") continue;
      const figWhite = fig === fig.toLowerCase();
      const playerWhite = player === "white";
      if (figWhite !== playerWhite) continue;
      
      for (let di = 0; di < 8; di++) {
        for (let dj = 0; dj < 8; dj++) {
          if (i === di && j === dj) continue;
          if (moveSavesKing([i, j], [di, dj], player)) return false;
        }
      }
    }
  }
  return true;
}

function moveSavesKing(startPos, endPos, player) {
  const fig = board[startPos[0]][startPos[1]];
  if (fig === ".") return false;
  if (!checkMove(startPos, endPos)) return false;

  const oldBoard = JSON.parse(JSON.stringify(board));
  const oldKingPos = player === "white" ? [...whiteKingPos] : [...blackKingPos];

  board[endPos[0]][endPos[1]] = board[startPos[0]][startPos[1]];
  board[startPos[0]][startPos[1]] = ".";
  
  if ((fig === 'k' && player === "white") || (fig === 'K' && player === "black")) {
    if (player === "white") whiteKingPos = [...endPos];
    else blackKingPos = [...endPos];
  }
  
  const kPos = player === "white" ? whiteKingPos : blackKingPos;
  const stillCheck = kingInCheck(kPos, player);
  
  board = oldBoard;
  if (player === "white") whiteKingPos = oldKingPos;
  else blackKingPos = oldKingPos;
  
  return !stillCheck;
}

function showCheckMsg(player) {
  const msg = document.createElement("div");
  msg.className = "check-message";
  msg.textContent = `${player.charAt(0).toUpperCase() + player.slice(1)} king is in check!`;
  const oldMsg = document.querySelector(".check-message");
  if (oldMsg) oldMsg.remove();
  document.body.appendChild(msg);
  setTimeout(() => { msg.remove(); }, 3000);
}

function showMateMsg(player) {
  const win = player === "white" ? "Black" : "White";
  const msg = document.createElement("div");
  msg.className = "checkmate-message";
  msg.textContent = `Checkmate! ${win} wins!`;
  document.body.appendChild(msg);
  lockPieces();
}

function lockPieces() {
  const figs = document.getElementsByClassName("piece");
  for (const f of figs) {
    f.style.pointerEvents = "none";
  }
}

function lightKing(player) {
  const kPos = player === "white" ? whiteKingPos : blackKingPos;
  const cell = document.getElementById(`${kPos[0] + 1}${kPos[1] + 1}`);
  cell.style.backgroundColor = "rgba(255, 0, 0, 0.5)";
  setTimeout(() => {
    const light = (kPos[0] + kPos[1]) % 2 === 0;
    cell.style.backgroundColor = light ? "#f0d9b5" : "#b58863";
  }, 1000);
}

function showMoves(startPos) {
  clearLights();
  const fig = board[startPos[0]][startPos[1]];
  const figWhite = fig === fig.toLowerCase();
  const turnWhite = turn === "white";
  if (figWhite !== turnWhite) return;
  
  for (let i = 0; i < 8; i++) {
    for (let j = 0; j < 8; j++) {
      const endPos = [i, j];
      if (i === startPos[0] && j === startPos[1]) continue;
      if (checkMove(startPos, endPos)) {
        if (moveBreaksKing(startPos, endPos)) continue;
        lightCell(endPos);
      }
    }
  }
}

function moveBreaksKing(startPos, endPos) {
  const fig = board[startPos[0]][startPos[1]];
  const oldBoard = JSON.parse(JSON.stringify(board));
  const oldKingPos = turn === "white" ? [...whiteKingPos] : [...blackKingPos];

  board[endPos[0]][endPos[1]] = board[startPos[0]][startPos[1]];
  board[startPos[0]][startPos[1]] = ".";
  
  if ((fig === 'k' && turn === "white") || (fig === 'K' && turn === "black")) {
    if (turn === "white") whiteKingPos = [...endPos];
    else blackKingPos = [...endPos];
  }
  
  const kPos = turn === "white" ? whiteKingPos : blackKingPos;
  const kingCheck = kingInCheck(kPos, turn);
  
  board = oldBoard;
  if (turn === "white") whiteKingPos = oldKingPos;
  else blackKingPos = oldKingPos;
  
  return kingCheck;
}

function lightCell(pos) {
  const cell = document.getElementById(`${pos[0] + 1}${pos[1] + 1}`);
  const lightElem = document.createElement("div");
  lightElem.className = "move-highlight";
  cell.appendChild(lightElem);
  litCells.push(cell);
}

function clearLights() {
  document.querySelectorAll(".move-highlight").forEach(el => el.remove());
  litCells = [];
}

function initGame() {
  startGame();
  setClicks();
}

window.addEventListener('DOMContentLoaded', initGame);
