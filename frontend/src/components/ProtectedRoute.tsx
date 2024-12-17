import React from "react";
import { Navigate } from "react-router-dom";
import { ProtectedRouteProps } from "./Interface/UtilsInterface";
import { UserInfo } from "./Interface/BusinessObjectInterface";
const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ element, allowedRoles }) => {

    const userInfoString = localStorage.getItem('userInfo');
    const userInfo: UserInfo = userInfoString ? JSON.parse(userInfoString) : null;

    if (userInfo === null) {
        return <Navigate to="/403" replace />;
    }

    if (!allowedRoles.includes(userInfo.role)) {
        return <Navigate to="/403" replace />;
    }

    return element;
};

export default ProtectedRoute;
