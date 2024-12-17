import React, {ChangeEvent, useEffect, useRef, useState} from "react";
import {Button, Card, Col, Container, Form, FormControl, FormGroup, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faAddressBook,
    faAddressCard,
    faCalendarDays,
    faCircleDot,
    faEnvelope,
    faFaceSmile,
    faImage,
    faLandmark,
    faUser
} from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import {Link, useNavigate} from "react-router-dom";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {SubmitHandler, useForm} from "react-hook-form";
import {Toast_Custom} from "../../Utils/Toast_Custom";
import {CreateEmployeeSchema} from "../../Interface/ValidationSchema/ValidationSchemas";
import LoadingOverlay from "../../Utils/LoadingOverlay/LoadingOverlay";
import items from "../../../data/location.json";
import BASE_URL from "../../Api/BaseApi";

type InputData = z.infer<typeof CreateEmployeeSchema>;

const CreateEmployee = () => {
        // State to track loading status
        const [isLoading, setIsLoading] = useState<boolean>(false);

        // Importing the json-form-data library
        const jsonFormData = require('json-form-data');

        // Reference to the file input element
        const fileInputRef = useRef<HTMLInputElement>(null);

        // Get the current date in ISO format
        const currentDate = new Date().toISOString().split("T")[0];

        // Navigation hook
        const nav = useNavigate();

        // Form handling using react-hook-form
        const {
            register,
            handleSubmit,
            reset,
            setValue,
            formState: {errors},
            watch
        } = useForm<InputData>({
            resolver: zodResolver(CreateEmployeeSchema),
        });

        // Set the document title
        document.title = "Create Employee";

        // Effect to load the default image on component mount
        useEffect(() => {
            loadDefaultImage().then(() => console.log("Load default image success"))
        }, []);

        // Function to load the default image
        const loadDefaultImage = async () => {
            // Try to fetch the default image from the public URL
            try {
                const response = await fetch(`${process.env.PUBLIC_URL}/assets/manager.png`);
                const blob = await response.blob();

                // Create a file object with the fetched image
                const file = new File([blob], "picture.png", {type: blob.type});

                // Set the value of the file input to the fetched file
                setValue("file", file);
            } catch (error) {
                // Log an error message if there's an issue loading the default image
                console.error("Error loading default image:", error);
            }
        };

        // Function to handle resetting the form and loading default image
        const handleReset = () => {
            reset();
            loadDefaultImage().then(() => console.log("Load default image success"));
        };

        // Function to handle image change
        const handleImageChange = (event: ChangeEvent<HTMLInputElement>) => {
            if (event.target.files && event.target.files[0]) {
                setValue('file', event.target.files[0]);
            }
        };

        /**
         * Function to format the request data for updating a schedule
         * @param data - The form data to be formatted
         * @returns The formatted request data object
         */
        const formatRequestData = (data: InputData) => {
            const {city, district, ward, streetAddress, ...restData} = data;
            const address = watch("city") + ", " + watch("district") + ", " + watch("ward") + ", " + watch("streetAddress");
            return {...restData, address: address};
        };


        /**
         * Function to handle input and allow only numeric characters
         * @param e - The event object containing the input element
         */
        const handleInput1 = (e: React.ChangeEvent<HTMLInputElement>) => {
            e.target.value = e.target.value.replace(/[^0-9]/g, '');
        };

        const onSubmit: SubmitHandler<InputData> = async (data) => {
            setIsLoading(true);
            try {
                const token = localStorage.getItem("token");
                const requestData = formatRequestData(data);
                const formDataToSend = jsonFormData(requestData);
                await axios.request({
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "multipart/form-data",
                    },
                    method: "POST",
                    url: `${BASE_URL}/vaccination/employee`,
                    data: formDataToSend
                })
                    .then(async (response) => {
                        if (response.status === 200) {
                            Toast_Custom({
                                type: "success",
                                message: "Create employee successfully",
                            })
                            nav(`/employee/list`);
                        }
                    })
            } catch (error) {
                console.error("Error creating employee:", error);
                if (axios.isAxiosError(error) && error.response) {
                    if (error.response.data.message) {
                        Toast_Custom({
                            type: "error",
                            message: error.response.data.message
                        })
                    }
                } else {
                    Toast_Custom({
                        type: "error",
                        message: "An unknown error occurred"
                    })
                }
            } finally {
                setIsLoading(false);
            }
        };

        return (
            <Container>
                {isLoading ? <LoadingOverlay/> : null}
                <Row className="mt-4 mb-2">
                    <h3 style={{fontWeight: "bold"}}>CREATE EMPLOYEE</h3>
                </Row>
                <Card className="px-3 mb-5 border-2 ">
                    <Card.Body>
                        <Form onSubmit={handleSubmit(onSubmit)} onReset={handleReset}>
                            <Row className="mb-lg-4 mt-4">
                                <Col lg={4} className="mb-4 mb-lg-0">
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="employeeId" className="fw-bold">
                                            Employee id<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faAddressBook} className="me-3"/>
                                            <FormControl
                                                id="employeeId"
                                                type="text"
                                                readOnly
                                                disabled
                                                className="border-1 border-secondary-subtle"
                                            />
                                        </div>
                                    </FormGroup>
                                </Col>
                                <Col lg={4} className="mb-4 mb-lg-0">
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="employeeName" className="fw-bold">
                                            Employee name<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faAddressBook} className="me-3"/>
                                            <FormControl
                                                {...register("employeeName")}
                                                id="employeeName"
                                                type="text"
                                                isInvalid={!!errors?.employeeName}
                                                className={` ${!errors?.employeeName ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.employeeName && (
                                            <p className="text-danger m-0">{errors.employeeName.message}</p>
                                        )}
                                    </FormGroup>
                                </Col>
                                <Col lg={4} className="mb-4 mb-lg-0">
                                    <FormGroup className="text-start">
                                        <Form.Label className="fw-bold">Gender:</Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faFaceSmile} className="me-3"/>
                                            <div
                                                className="d-inline-flex align-items-center
                                                border border-1 border-secondary-subtle  rounded-2"
                                                style={{
                                                    width: "100%",
                                                    height: " 100%",
                                                    padding: "5.5px 0px",
                                                }}
                                            >
                                                <Row className="m-0 d-inline-flex">
                                                    <Col xs={6} className="">
                                                        <Form.Check {...register("gender")}
                                                                    type="radio"
                                                                    id="male"
                                                                    label="Male"
                                                                    value="Male"
                                                                    defaultChecked

                                                        />
                                                    </Col>
                                                    <Col xs={6}>
                                                        <Form.Check {...register("gender")}
                                                                    type="radio"
                                                                    id="female"
                                                                    label="Female"
                                                                    value="Female"
                                                        />
                                                    </Col>
                                                </Row>
                                            </div>
                                        </div>
                                    </FormGroup>
                                </Col>
                            </Row>

                            <Row className="mb-4">
                                <Col lg={4} className="mb-4 mb-lg-0">
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="dateOfBirth" className="fw-bold">
                                            Date of birth<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faCalendarDays} className="me-3"/>
                                            <FormControl
                                                {...register("dateOfBirth")}
                                                id="dateOfBirth"
                                                type="date"
                                                max={currentDate}
                                                isInvalid={!!errors?.dateOfBirth}
                                                className={` ${!errors?.dateOfBirth ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.dateOfBirth &&
                                            <p className="text-danger m-0">{errors.dateOfBirth.message}</p>}{}
                                    </FormGroup>
                                </Col>
                                <Col lg={4} className="mb-4 mb-lg-0">
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="phone" className="fw-bold">
                                            Phone<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faAddressCard} className="me-3"/>
                                            <FormControl
                                                {...register("phone")}
                                                id="phone"
                                                type="tel"
                                                onInput={handleInput1}
                                                isInvalid={!!errors?.phone}
                                                className={` ${!errors?.phone ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.phone && <p className="text-danger m-0">{errors.phone.message}</p>}{}
                                    </FormGroup>
                                </Col>
                                <Col lg={4}>
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="email" className="fw-bold">
                                            Email<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faEnvelope} className="me-3"/>
                                            <FormControl
                                                {...register("email")}
                                                id="email"
                                                type="email"
                                                isInvalid={!!errors?.email}
                                                className={` ${!errors?.email ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.email && <p className="text-danger m-0">{errors.email.message}</p>}
                                    </FormGroup>
                                </Col>
                            </Row>
                            <Row className="mb-lg-4">
                                <Form.Label className="fw-bold text-start">
                                    Address<span style={{color: "red"}}>(*):</span>
                                </Form.Label>
                                <Col lg={4} className="mb-4 mb-lg-0">
                                    <div className="d-flex justify-content-center align-items-center">
                                        <Form.Label className="m-0 me-2" htmlFor="city">
                                            City:
                                        </Form.Label>
                                        <Form.Select
                                            id="city"
                                            {...register("city")}
                                            isInvalid={!!errors.city}
                                            className={` ${!errors?.city ? 'border-secondary-subtle' : ''}`}
                                        >
                                            <option value="">Select City</option>
                                            {items?.map((c) => (
                                                <option key={c.Code} value={c.FullName}>
                                                    {c.FullName}
                                                </option>
                                            ))}
                                        </Form.Select>
                                    </div>
                                    {errors.city &&
                                        <p className="text-danger m-0 text-start">{errors.city.message?.toString()}</p>}
                                </Col>
                                <Col lg={4} className="mb-4 mb-lg-0">
                                    <div className="d-flex justify-content-center align-items-center">
                                        <Form.Label className="m-0 me-2" htmlFor="district">
                                            District:
                                        </Form.Label>
                                        <Form.Select
                                            id="district"
                                            {...register("district")}
                                            isInvalid={!!errors.district}
                                            className={` ${!errors?.district ? 'border-secondary-subtle' : ''}`}
                                        >
                                            <option value="">Select District</option>
                                            {watch("city") !== '' && items?.find((item) => item.FullName === watch("city"))?.District.map((d) => (
                                                <option key={d.Code} value={d.FullName}>
                                                    {d.FullName}
                                                </option>
                                            ))}
                                        </Form.Select>
                                    </div>
                                    {errors.district &&
                                        <p className="text-danger m-0 text-start">{errors.district.message?.toString()}</p>}
                                </Col>
                                <Col lg={4} className="mb-4 mb-lg-0">
                                    <div className="d-flex justify-content-center align-items-center">
                                        <Form.Label htmlFor="ward" className="m-0 me-2">
                                            Ward:
                                        </Form.Label>
                                        <Form.Select
                                            id="ward"
                                            {...register("ward")}
                                            isInvalid={!!errors.ward}
                                            className={` ${!errors?.ward ? 'border-secondary-subtle' : ''}`}
                                        >
                                            <option value="">Select Ward</option>
                                            {watch("district") !== '' &&
                                                items?.find((item) => item.FullName === watch("city"))?.District
                                                    .find((d) => d.FullName === watch("district"))?.Ward.map((w) => (
                                                    <option key={w.Code} value={w.FullName}>
                                                        {w.FullName}
                                                    </option>
                                                ))}
                                        </Form.Select>
                                    </div>
                                    {errors.ward &&
                                        <p className="text-danger m-0 text-start">{errors.ward.message?.toString()}</p>}
                                </Col>
                            </Row>
                            <Row className="mb-4">
                                <Col>
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="streetAddress">
                                            Street address:
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FormControl
                                                {...register("streetAddress")}
                                                id="streetAddress"
                                                type="text"
                                                isInvalid={!!errors.streetAddress}
                                                className={` ${!errors?.streetAddress ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.streetAddress && (
                                            <p className="text-danger m-0">{errors.streetAddress.message?.toString()}</p>
                                        )}
                                    </FormGroup>
                                </Col>
                            </Row>
                            <Row className="mb-4">
                                <Col lg={4} className="mb-4 mb-lg-0">
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="username" className="fw-bold">
                                            Username<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faUser} className="me-3"/>
                                            <FormControl
                                                {...register("username")}
                                                id="username"
                                                type="text"
                                                isInvalid={!!errors?.username}
                                                className={` ${!errors?.username ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.username && <p className="text-danger m-0">{errors.username.message}</p>}
                                    </FormGroup>
                                </Col>
                                <Col lg={4} className="mb-4 mb-lg-0">
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="position" className="fw-bold">
                                            Position<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faCircleDot} className="me-3"/>
                                            <Form.Select
                                                {...register("position")}
                                                id="position"
                                                defaultValue="ROLE_EMPLOYEE"
                                                isInvalid={!!errors?.position}
                                                className={` ${!errors?.position ? 'border-secondary-subtle' : ''}`}
                                            >
                                                <option value="ROLE_EMPLOYEE">Employee</option>
                                                {/*<option value="ROLE_ADMIN">Admin</option>*/}
                                            </Form.Select>
                                        </div>
                                        {errors.position && <p className="text-danger m-0">{errors.position.message}</p>}
                                    </FormGroup>
                                </Col>
                                <Col lg={4}>
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="workingPlace" className="fw-bold">
                                            Working place:
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faLandmark} className="me-3"/>
                                            <FormControl
                                                {...register("workingPlace")}
                                                id="workingPlace"
                                                type="text"
                                                isInvalid={!!errors?.workingPlace}
                                                className={` ${!errors?.workingPlace ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.workingPlace &&
                                            <p className="text-danger">{errors.workingPlace.message}</p>}
                                    </FormGroup>
                                </Col>
                            </Row>
                            <Row className="mb-4">
                                <Col lg={5}>
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="file" className="fw-bold">
                                            Profile Image
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faImage} className="me-3"/>
                                            <FormControl
                                                {...register("file")}
                                                id="file"
                                                type="file"
                                                accept="image/png, image/jpeg"
                                                onChange={handleImageChange}
                                                ref={fileInputRef}
                                                className={` ${!errors?.file ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                    </FormGroup>
                                </Col>
                            </Row>
                            <Row className="mb-4 justify-content-lg-start justify-content-center">
                                <Col xs={"auto"}>
                                    <Button
                                        type="submit"
                                        variant="success"
                                        className="text-white px-4">
                                        Save
                                    </Button>
                                </Col>
                                <Col xs={"auto"}>
                                    <Button
                                        type="reset"
                                        variant="info"
                                        className="text-white px-4">
                                        Reset
                                    </Button>
                                </Col>
                                <Col xs={"auto"}>
                                    <Link to={"/employee/list"} id="cancelBtn" hidden></Link>
                                    <Button type="button" variant="warning" className="text-white px-4"
                                            onClick={() => document.getElementById("cancelBtn")?.click()}>
                                        Cancel
                                    </Button>
                                </Col>
                            </Row>
                        </Form>
                    </Card.Body>
                </Card>
            </Container>
        );
    }
;

export default CreateEmployee;
