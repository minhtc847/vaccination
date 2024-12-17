import React, {useEffect, useState} from "react";
import {Button, Card, Col, Container, Form, FormControl, FormGroup, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faAddressBook,
    faAddressCard,
    faCalendarDays,
    faEnvelope,
    faFaceSmile,
    faKeyboard,
    faRedo
} from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import {Link, useNavigate, useParams} from "react-router-dom";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {SubmitHandler, useForm} from "react-hook-form";
import {Toast_Custom} from "../../Utils/Toast_Custom";
import {UpdateCustomerSchema} from "../../Interface/ValidationSchema/ValidationSchemas";
import LoadingOverlay from "../../Utils/LoadingOverlay/LoadingOverlay";
import items from "../../../data/location.json";
import BASE_URL from "../../Api/BaseApi";

type InputData = z.infer<typeof UpdateCustomerSchema>;

const UpdateCustomer = () => {
        const {id} = useParams<{ id: string }>();
        const currentDate = new Date().toISOString().split("T")[0];
        const [captcha, setCaptcha] = useState('');
        const [captchaError, setCaptchaError] = useState<string>("");
        const [isLoading, setIsLoading] = useState<boolean>(false);
        const [render, setRender] = useState(0);
        const nav = useNavigate();
        const {
            register,
            handleSubmit,
            reset,
            formState: {errors},
            setValue,
            watch,
        } = useForm<InputData>({
            resolver: zodResolver(UpdateCustomerSchema),
        });
        const generateCaptcha = () => {
            const newCaptcha = Math.floor(10000 + Math.random() * 90000).toString();
            setCaptcha(newCaptcha);
        };
        document.title = "Update Customer";

        useEffect(() => {
            generateCaptcha();
        }, []);

        useEffect(() => {
            const fetchCustomer = async () => {
                if (id) {
                    try {
                        const token = localStorage.getItem("token");
                        const response = await axios.request({
                            headers: {
                                Authorization: `Bearer ${token}`,
                            },
                            method: "GET",
                            url: `${BASE_URL}/vaccination/customer/${id}`,
                        });
                        const customerData = response.data;
                        reset(customerData);
                        // Extract location information from customer data
                        const location = customerData.address.split(",");

                        // Set city, district, ward, and street address values
                        setValue("city", location[0].trim());
                        setValue("district", location[1].trim());
                        setValue("ward", location[2].trim());
                        setValue("streetAddress", location[3].trim());
                        setValue("gender", customerData.gender);
                    } catch (error) {
                        console.error("Error fetching schedule data:", error);
                    }
                }
            }
            fetchCustomer();
        }, [id, reset, setValue, render]);

        const city = watch("city");
        const district = watch("district");

        const handleReset = () => {
            setRender(prev => prev + 1);
            generateCaptcha();
            setCaptchaError("");
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
                if (data.captcha !== captcha) {
                    setCaptchaError("Incorrect CAPTCHA. Please try again.");
                    generateCaptcha();
                    return;
                }
                setCaptchaError("");
                UpdateCustomerSchema.parse(data);
                const token = localStorage.getItem("token");
                const requestData = formatRequestData(data);
                await axios.request({
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                    method: "PUT",
                    url: `${BASE_URL}/vaccination/customer/update-customer/${id}`,
                    data: requestData,
                })
                    .then(async (response) => {
                        if (response.status === 200) {
                            Toast_Custom({
                                type: "success",
                                message: "Update customer successfully",
                            })
                            nav('/customer/list');
                        }
                    })
            } catch (error) {
                console.error("Error updating customer:", error);
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
                setTimeout(() => {
                    setIsLoading(false);
                }, 500)
            }
        };

        return (
            <Container>
                {isLoading ? <LoadingOverlay/> : null}
                <Row className="mt-4 mb-2">
                    <h3 style={{fontWeight: "bold"}}>UPDATE CUSTOMER INFORMATION</h3>
                </Row>
                <Form onSubmit={handleSubmit(onSubmit)} onReset={handleReset}>
                    <Card className="px-3 mb-5 border-2 ">
                        <Card.Body>
                            <h4 className="text-lg-start mb-4">Personal Information</h4>
                            <Row className="mb-lg-4 mt-4">
                                <Col lg={4} className="mb-4 mb-lg-0">
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="employeeName" className="fw-bold">
                                            Full name<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faAddressBook} className="me-3"/>
                                            <FormControl
                                                {...register("employeeName")}
                                                id="employeeName"
                                                type="text"
                                                value={watch("employeeName")}
                                                isInvalid={!!errors?.employeeName}
                                                className={` ${!errors?.employeeName ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.employeeName && (
                                            <p className="text-danger m-0">{errors.employeeName.message}</p>
                                        )}
                                    </FormGroup>
                                </Col>
                                <Col xs={6} lg={4} className="mb-4 mb-lg-0">
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
                                                value={watch("dateOfBirth")}
                                                isInvalid={!!errors?.dateOfBirth}
                                                className={` ${!errors?.dateOfBirth ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.dateOfBirth &&
                                            <p className="text-danger m-0">{errors.dateOfBirth.message}</p>}{}
                                    </FormGroup>
                                </Col>
                                <Col xs={6} lg={4} className="mb-4 mb-lg-0">
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
                                                <Row className="m-0">
                                                    <Col xs={6} className="">
                                                        <Form.Check {...register("gender")}
                                                                    type="radio"
                                                                    id="male"
                                                                    label="Male"
                                                                    value="Male"
                                                                    checked={watch()?.gender === "Male"}
                                                        />
                                                    </Col>
                                                    <Col xs={6}>
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
                                        <Form.Label htmlFor="identityCard" className="fw-bold">
                                            Identity card<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faAddressBook} className="me-3"/>
                                            <FormControl {...register("identityCard")}
                                                         id="identityCard"
                                                         type="text"
                                                         value={watch("identityCard")}
                                                         isInvalid={!!errors?.identityCard}
                                                         className={` ${!errors?.identityCard ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.identityCard && (
                                            <p className="text-danger m-0">{errors.identityCard.message}</p>
                                        )}
                                    </FormGroup>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>
                    <Card className="px-3 mb-5 border-2 ">
                        <Card.Body>
                            <h4 className="text-lg-start mb-4">Account Information</h4>
                            <Row className="mb-4">
                                <Col lg={4} className="mb-4 mb-lg-0">
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
                                                value={watch("email")}
                                                isInvalid={!!errors?.email}
                                                className={` ${!errors?.email ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.email && <p className="text-danger m-0">{errors.email.message}</p>}
                                    </FormGroup>
                                </Col>
                                <Col lg={4}>
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
                                                isInvalid={!!errors?.phone}
                                                className={` ${!errors?.phone ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {errors.phone && <p className="text-danger">{errors.phone.message}</p>}
                                    </FormGroup>
                                </Col>
                            </Row>
                            <Row className="mb-4">
                                <Col md={4}>
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="captcha" className="fw-bold">
                                            Captcha<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faKeyboard} className="me-3"/>
                                            <FormControl
                                                {...register("captcha")}
                                                id="captcha"
                                                type="text"
                                                isInvalid={captchaError !== ""}
                                                className={` ${captchaError === "" ? 'border-secondary-subtle' : ''}`}
                                            />
                                        </div>
                                        {captchaError && <p className="text-danger">{captchaError}</p>}
                                    </FormGroup>
                                </Col>
                                <Col md={4}>
                                    <FormGroup className="text-start">
                                        <Form.Label className="fw-bold">
                                            Code:
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <Button disabled variant="light"
                                                    className="fw-bold border border-end-0 border-secondary-subtle rounded-0"
                                            >{captcha}</Button>
                                            <Button onClick={generateCaptcha} variant="light"
                                                    className="border border-secondary-subtle rounded-0">
                                                <FontAwesomeIcon icon={faRedo}/>
                                            </Button>
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
                                    <Link to={"/customer/list"} id="cancelBtn" hidden></Link>
                                    <Button type="button" variant="warning" className="text-white px-4"
                                            onClick={() => document.getElementById("cancelBtn")?.click()}>
                                        Cancel
                                    </Button>
                                </Col>
                            </Row>
                        </Card.Body>
                    </Card>
                </Form>
            </Container>
        );
    }
;

export default UpdateCustomer;
