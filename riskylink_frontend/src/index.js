import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import {
  createBrowserRouter,
  RouterProvider
} from "react-router-dom";
import UploadDatasets from './UploadDatasets';
import UploadOntologies from './UploadOntologies';

const router = createBrowserRouter([
  {
    path: "/",
    element: App(),
  },
  {
    path: "UploadDatasets",
    element: UploadDatasets(),
  },
  {
    path: "UploadOntologies",
    element: UploadOntologies(),
  }
]);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <RouterProvider router={router} />
);




// createRoot(document.getElementById("root")).render(
//   <RouterProvider router={router} />
// );