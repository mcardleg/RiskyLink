import { v4 as uuid } from 'uuid';

let cookieName = "sessionID=";

function SetCookie(life) {
  console.log("SetCookie");

  const d = new Date();
  d.setTime(d.getTime() + (life * 60 * 60 * 1000));
  let expires = "expires="+d.toUTCString();
  document.cookie = cookieName + uuid() + ";" + expires + ";path=/";
  console.log(document.cookie);
}

function GetCookie() {
  let ca = document.cookie.split(';');
  for(let i = 0; i < ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) === ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(cookieName) === 0) {
      return c.substring(cookieName.length, c.length);
    }
  }
  return "";
}

function CheckCookie() {
  console.log("CheckCookie");
  console.log(document.cookie);
  let cookie = GetCookie();
  if (cookie === "" | cookie === null) {
    return false;
  }
  return true;
}

// function GetSessionID() {

// }

function DeleteSessionID() {
  SetCookie(0);
}

function EnsureSessionID() {
  if (!CheckCookie()) {
    SetCookie(3);
  }
}

function RedirectIfNoSessionID() {
  if (!CheckCookie()) {
    alert("Sorry! You for some reason you don't have a Session ID. You'll be redirected to the homepage to start again.");
    window.location.href = '/';
  }
}

export { GetCookie, EnsureSessionID, RedirectIfNoSessionID, DeleteSessionID };
