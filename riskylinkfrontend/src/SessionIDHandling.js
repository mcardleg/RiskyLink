import { v4 as uuid } from 'uuid';
import { useEffect } from "react";
import { backendURL } from './App';


let cookieName = "sessionID=";

function SetSessionID(life) {
  const d = new Date();
  d.setTime(d.getTime() + (life * 60 * 60 * 1000));
  let expires = "expires=" + d.toUTCString();
  document.cookie = cookieName + uuid() + ";" + expires + ";path=/";

  useEffect(() => {
    fetch(backendURL + 'startSession', {
      method: 'GET',
      headers: {
        'sessionID': GetSessionID(),
      }
    });
  }, []);

  console.log(GetSessionID());
}

function GetSessionID() {
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

function CheckSessionID() {
  let cookie = GetSessionID();
  if (cookie === "" | cookie === null) {
    return false;
  }
  return true;
}

function DeleteSessionID() {
  SetSessionID(0);
}

function EnsureSessionID() {
  if (!CheckSessionID()) {
    SetSessionID(3);
  }
}

function RedirectIfNoSessionID() {
  if (!CheckSessionID()) {
    alert("Sorry! For some reason you haven't been assigned a Session ID. You'll be redirected to the homepage to start again.");
    window.location.href = '/';
    return true;
  }
  return false;
}

export { GetSessionID, EnsureSessionID, RedirectIfNoSessionID, DeleteSessionID };
