import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import Home from './home/Home';
import UploadDatasets from './uploads/UploadDatasets';
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import UploadOntologies from './uploads/UploadOntologies';

const router = createBrowserRouter([
  {
    path: "/",
    element: <Home />,
  },
  {
    path: "UploadDatasets",
    element: <UploadDatasets />,
  },
  {
    path: "UploadOntologies",
    element: <UploadOntologies />
  }
]);

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <RouterProvider router={router} />
);
