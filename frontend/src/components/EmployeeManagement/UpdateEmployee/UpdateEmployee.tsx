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
    faLandmark
} from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import {Link, useNavigate, useParams} from "react-router-dom";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {SubmitHandler, useForm} from "react-hook-form";
import {Toast_Custom} from "../../Utils/Toast_Custom";
import {UpdateEmployeeSchema} from "../../Interface/ValidationSchema/ValidationSchemas";
import LoadingOverlay from "../../Utils/LoadingOverlay/LoadingOverlay";
import items from "../../../data/location.json";
import BASE_URL from "../../Api/BaseApi";

type InputData = z.infer<typeof UpdateEmployeeSchema>;

const UpdateEmployee = () => {
        const {id} = useParams<{ id: string }>();
        const fileInputRef = useRef<HTMLInputElement>(null);
        const jsonFormData = require('json-form-data');
        const [render, setRender] = useState(0);
        const [isLoading, setIsLoading] = useState<boolean>(false);
        const nav = useNavigate();
        const {
            register, handleSubmit, reset,
            setValue, watch, formState: {errors},
        } = useForm<InputData>({
            resolver: zodResolver(UpdateEmployeeSchema),
        });
        document.title = "Update Employee";

        useEffect(() => {
            const fetchEmployee = async () => {
                if (id) {
                    try {
                        const token = localStorage.getItem("token");
                        const response =
                            await axios.get(`${BASE_URL}/vaccination/employee/${id}`, {
                                headers: {Authorization: `Bearer ${token}`},
                            });
                        // Reset the form with schedule data
                        const employeeData = response.data;
                        reset(employeeData);

                        // Extract location information from employee data
                        const location = employeeData.address.split(",");

                        // Set city, district, ward, and street address values
                        setValue("city", location[0].trim());
                        setValue("district", location[1].trim());
                        setValue("ward", location[2].trim());
                        setValue("streetAddress", location[3].trim());

                        setValue("gender", employeeData.gender)
                    } catch (error) {
                        console.error("Error fetching employee data:", error);
                    }
                }
            };
            fetchEmployee();
        }, [id, reset, render, setValue]);

        const city = watch("city");
        const district = watch("district");

        const handleImageChange = (event: ChangeEvent<HTMLInputElement>) => {
            if (event.target.files && event.target.files[0]) {
                setValue('file', event.target.files[0]);
            }
        };

        // Function to handle resetting the component
        const handleReset = () => {
            setRender(render + 1);
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

        const onSubmit: SubmitHandler<InputData> = async (data) => {
            setIsLoading(true);
            try {
                UpdateEmployeeSchema.parse(data);
                const token = localStorage.getItem("token");
                const requestData = formatRequestData(data);
                const formDataToSend = jsonFormData(requestData);
                await axios.request({
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "multipart/form-data"
                    },
                    method: "PUT",
                    url: `${BASE_URL}/vaccination/employee/${id}`,
                    data: formDataToSend
                }).then((response) => {
                    if (response.status === 200) {
                        Toast_Custom({
                            type: "success",
                            message: "Update employee successfully",
                        })
                        nav("/employee/list");
                    }
                })
            } catch (error) {
                console.error("Error updating employee:", error);
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
                    <h3 style={{fontWeight: "bold"}}>UPDATE EMPLOYEE</h3>
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
                                                value={id}
                                                readOnly
                                                disabled
                                                className="border-1  border-secondary-subtle"
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
                                            <FormControl {...register("employeeName")}
                                                         id="employeeName"
                                                         type="text"
                                                         value={watch("employeeName")}
                                                         isInvalid={!!errors.employeeName}
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
                                                    <Col className="">
                                                        <Form.Check {...register("gender")}
                                                                    type="radio"
                                                                    id="male"
                                                                    label="Male"
                                                                    value="Male"
                                                                    checked={watch()?.gender === "Male"}
                                                        />
                                                    </Col>
                                                    <Col>
                                                        <Form.Check {...register("gender")}
                                                                    type="radio"
                                                                    id="female"
                                                                    label="Female"
                                                                    value="Female"
                                                                    checked={watch()?.gender === "Female"}
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
                                                value={watch("dateOfBirth")}
                                                isInvalid={!!errors.dateOfBirth}
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
                                                value={watch("phone")}
                                                isInvalid={!!errors.phone}
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
                                                type="text"
                                                value={watch("email")}
                                                isInvalid={!!errors.email}
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
                                        <p className="text-danger m-0 text-start">{errors.city.message}</p>}
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
                                            {city !== '' && items?.find((item) => item.FullName === city)?.District.map((d) => (
                                                <option key={d.Code} value={d.FullName}>
                                                    {d.FullName}
                                                </option>
                                            ))}
                                        </Form.Select>
                                    </div>
                                    {errors.district &&
                                        <p className="text-danger m-0 text-start">{errors.district.message}</p>}
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
                                            {district !== '' &&
                                                items?.find((item) => item.FullName === city)?.District
                                                    .find((d) => d.FullName === district)?.Ward.map((w) => (
                                                    <option key={w.Code} value={w.FullName}>
                                                        {w.FullName}
                                                    </option>
                                                ))}
                                        </Form.Select>
                                    </div>
                                    {errors.ward &&
                                        <p className="text-danger m-0 text-start">{errors.ward.message}</p>}
                                </Col>
                            </Row>
                            <Row className="mb-4">
                                <Col>
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="streetAddress">
                                            Street address:
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FormControl {...register("streetAddress")}
                                                         id="streetAddress"
                                                         type="text"
                                                         isInvalid={!!errors.streetAddress}
                                                         className={` ${!errors?.streetAddress ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.streetAddress && (
                                            <p className="text-danger m-0">{errors.streetAddress.message}</p>
                                        )}
                                    </FormGroup>
                                </Col>
                            </Row>

                            <Row className="mb-4">
                                <Col lg={6} className="mb-4 mb-lg-0">
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="position" className="fw-bold">
                                            Position<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faCircleDot} className="me-3"/>
                                            <Form.Select {...register("position")}
                                                         id="position"
                                                         value={watch("position")}
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
                                <Col lg={6}>
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
                                                value={watch("workingPlace")}
                                                isInvalid={!!errors.workingPlace}
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
                                        className="text-white px-4 border-1 "
                                    >
                                        Save
                                    </Button>
                                </Col>
                                <Col xs={"auto"}>
                                    <Button
                                        type="reset"
                                        variant="info"
                                        className="text-white px-4 border-1 "
                                    >
                                        Reset
                                    </Button>
                                </Col>
                                <Col xs={"auto"}>
                                    <Link to={"/employee/list"} id="cancelBtn" hidden></Link>
                                    <Button
                                        type="button"
                                        variant="warning"
                                        className="text-white px-4 border-1 "
                                        onClick={() => document.getElementById("cancelBtn")?.click()}
                                    >
                                        Cancel
                                    </Button>
                                </Col>
                            </Row>
                        </Form>
                    </Card.Body>
                </Card>
            </Container>
        )
            ;
    }
;

export default UpdateEmployee;
