import React, {useEffect, useState} from "react";
import {Button, Card, Col, Container, Form, FormControl, FormGroup, FormSelect, Row} from "react-bootstrap";
import axios from "axios";
import {Link, useNavigate, useParams} from "react-router-dom";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {SubmitHandler, useForm} from "react-hook-form";
import {Toast_Custom} from "../../Utils/Toast_Custom";
import {UpdateScheduleSchema} from "../../Interface/ValidationSchema/ValidationSchemas";
import LoadingOverlay from "../../Utils/LoadingOverlay/LoadingOverlay";
import items from "../../../data/location.json";
import BASE_URL from "../../Api/BaseApi";

type InputData = z.infer<typeof UpdateScheduleSchema>;

type Vaccine = {
    id: number;
    vaccineName: string;
    status: boolean;
}

function UpdateSchedule() {
    // Current date in ISO format
    const currentDate = new Date().toISOString().split("T")[0];

    // State for storing vaccine data
    const [vaccine, setVaccine] = useState([]);

    // Get the id parameter from the URL
    const {id} = useParams<{ id: string }>();

    // State for re-rendering the component
    const [render, setRender] = useState(0);

    // State for tracking loading status
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const nav = useNavigate();

    const blockInvalidChar = (e: React.KeyboardEvent<HTMLInputElement>) => ['e', 'E', '+', '-', '.'].includes(e.key) && e.preventDefault();

    // Define the useForm hook with InputData type and zodResolver for validation
    document.title = "Update Injection Schedule";
    const {
        register, // Function to register inputs
        handleSubmit, // Function to handle form submission
        reset, // Function to reset form fields
        watch, // Function to watch form inputs
        setValue, // Function to set form values
        formState: {errors} // Object to hold form validation errors
    } = useForm<InputData>({
        resolver: zodResolver(UpdateScheduleSchema), // Using zodResolver for schema-based form validation
    });

    // Effect hook to fetch vaccine and schedule data
    useEffect(() => {
        // Function to fetch vaccine data
        const fetchVaccineData = async () => {
            const token = localStorage.getItem("token");
            const response = await axios.get(`${BASE_URL}/vaccination/vaccine?pageSize=100`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            });
            const data = response.data;
            setVaccine(data.content.filter((vaccine: Vaccine) => vaccine.status));
        };

        // Function to fetch schedule data
        const fetchScheduleData = async () => {
            if (id) {
                try {
                    const token = localStorage.getItem("token");
                    const response = await axios.get(`${BASE_URL}/vaccination/schedule/${id}`, {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        }
                    });
                    // Reset the form with schedule data
                    const scheduleData = response.data;
                    reset(scheduleData);

                    // Extract location information from schedule data
                    const location = scheduleData.place.split(",");

                    // Set city, district, ward, and street address values
                    setValue("city", location[0].trim());
                    setValue("district", location[1].trim());
                    setValue("ward", location[2].trim());
                    setValue("streetAddress", location[3].trim());
                } catch (error) {
                    console.error("Error fetching schedule data:", error);
                }
            }
        };

        // Check if there is no vaccine data, then fetch vaccine data
        if (vaccine.length === 0) {
            fetchVaccineData();
        }
        // Always fetch schedule data
        fetchScheduleData();
    }, [id, reset, render, vaccine.length, setValue]);

    // Get the current values of city and district from the form
    const city = watch("city");
    const district = watch("district");

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
        const location = watch("city") + ", " + watch("district") + ", " + watch("ward") + ", " + watch("streetAddress");
        return {...restData, place: location};
    };

    /**
     * Handles the form submission for updating a schedule
     * @param data - The form data to be submitted
     */
    const onSubmit: SubmitHandler<InputData> = async (data) => {
        // Set loading state to true
        setIsLoading(true);

        try {
            // Get the token from local storage
            const token = localStorage.getItem("token");
            // Format the request data before sending to the server
            const requestData = formatRequestData(data);
            // Send a PUT request to update the schedule
            await axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                method: "PUT",
                url: `${BASE_URL}/vaccination/schedule/${id}`,
                data: requestData,
            })
                .then((response) => {
                    // If the response status is 200, show success message and reset the form
                    if (response.status === 200) {
                        Toast_Custom({
                            type: "success",
                            message: "Update schedule successfully",
                        });
                        nav("/schedule/list");
                    }
                });
        } catch (error) {
            // Handle errors from the API request
            if (axios.isAxiosError(error) && error.response) {
                if (error.response.data.message) {
                    Toast_Custom({
                        type: "error",
                        message: error.response.data.message
                    });
                }
            } else {
                Toast_Custom({
                    type: "error",
                    message: "An unknown error occurred"
                });
            }
        } finally {
            // Set loading state to false after a delay
            setTimeout(() => {
                setIsLoading(false);
            }, 500);
        }
    };

    return (
        <Container>
            {isLoading ? <LoadingOverlay/> : null}
            <Row className="mt-4 mb-2">
                <h4 style={{fontWeight: "bold"}}>UPDATE SCHEDULE</h4>
            </Row>
            <Card className="p-3">
                <Card.Body>
                    <Form onSubmit={handleSubmit(onSubmit)} onReset={handleReset}>
                        <Row className="mb-4">
                            <Col md={4}>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="vaccineId" className="fw-bold">
                                        Vaccine Name<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormSelect
                                            {...register("vaccineId", {valueAsNumber: true})}
                                            id="vaccineId" isInvalid={!!errors.vaccineId}
                                        >
                                            <option value={0}>Select Vaccine</option>
                                            {Array.isArray(vaccine) && vaccine.map((v: Vaccine) => (
                                                <option key={v.id} value={v.id}>
                                                    {v.vaccineName}
                                                </option>
                                            ))}
                                        </FormSelect>
                                    </div>
                                    {errors.vaccineId && (
                                        <p className="text-danger">{errors.vaccineId.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col lg={4} sm={6} className="mb-lg-0 mb-4">
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="startDate" className="fw-bold">
                                        From<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl
                                            {...register("startDate")}
                                            id="startDate"
                                            type="date"
                                            min={currentDate}
                                            isInvalid={!!errors.startDate}
                                            value={watch("startDate") || currentDate}
                                        />
                                    </div>
                                    {errors.startDate && (
                                        <p className="text-danger">{errors.startDate.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col lg={4} sm={6}>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="endDate" className="fw-bold">
                                        To<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl
                                            {...register("endDate")}
                                            id="endDate"
                                            type="date"
                                            min={currentDate}
                                            isInvalid={!!errors.endDate}
                                            value={watch("endDate") || currentDate}
                                        />
                                    </div>
                                    {errors.endDate && (
                                        <p className="text-danger">{errors.endDate.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label className="fw-bold">
                                        Place<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <Row className="mb-0 mb-lg-4">
                                        <Col lg={4} className="mb-4 mb-lg-0">
                                            <div className="d-flex justify-content-center align-items-center">
                                                <Form.Label className="me-lg-2" htmlFor="city"
                                                            style={{margin: "0 50px 0 0"}}>
                                                    City:
                                                </Form.Label>
                                                <Form.Select id="city" {...register("city")}
                                                             isInvalid={!!errors.city}>
                                                    <option value="">Select City</option>
                                                    {items?.map((c) => (
                                                        <option key={c.Code} value={c.FullName}>
                                                            {c.FullName}
                                                        </option>
                                                    ))}
                                                </Form.Select>
                                            </div>
                                            {errors.city &&
                                                <p className="text-danger m-0">{errors.city.message}</p>}
                                        </Col>
                                        <Col lg={4} className="mb-4 mb-lg-0">
                                            <div className="d-flex justify-content-center align-items-center">
                                                <Form.Label className="me-lg-2" htmlFor="district"
                                                            style={{margin: "0 28px 0 0"}}>
                                                    District:
                                                </Form.Label>
                                                <Form.Select id="district" {...register("district")}
                                                             isInvalid={!!errors.district}>
                                                    <option value="">Select District</option>
                                                    {city !== '' && items?.find((item) => item.FullName === city)?.District.map((d) => (
                                                        <option key={d.Code} value={d.FullName}>
                                                            {d.FullName}
                                                        </option>
                                                    ))}
                                                </Form.Select>
                                            </div>
                                            {errors.district &&
                                                <p className="text-danger m-0">{errors.district.message}</p>}
                                        </Col>
                                        <Col lg={4} className="mb-4 mb-lg-0">
                                            <div className="d-flex justify-content-center align-items-center">
                                                <Form.Label htmlFor="ward" className="me-lg-2"
                                                            style={{margin: "0 40px 0 0"}}>
                                                    Ward:
                                                </Form.Label>
                                                <Form.Select id="ward" {...register("ward")} isInvalid={!!errors.ward}>
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
                                                <p className="text-danger m-0">{errors.ward.message}</p>}
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col xs={12}>
                                            <FormGroup className="text-start">
                                                <Form.Label htmlFor="streetAddress">
                                                    Street address:
                                                </Form.Label>
                                                <div className="d-flex align-items-center">
                                                    <FormControl {...register("streetAddress")}
                                                                 id="streetAddress"
                                                                 type="text"
                                                                 isInvalid={!!errors.streetAddress}
                                                    />
                                                </div>
                                                {errors.streetAddress && (
                                                    <p className="text-danger m-0">{errors.streetAddress.message}</p>
                                                )}
                                            </FormGroup>
                                        </Col>
                                    </Row>
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <Row>
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="injectPerDay" className="fw-bold">
                                            Inject Per Day<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FormControl {...register("injectPerDay", {valueAsNumber: true})}
                                                         id="injectPerDay"
                                                         type="number"
                                                         min={1}
                                                         value={watch("injectPerDay")}
                                                         onKeyDown={blockInvalidChar}
                                                         isInvalid={!!errors.injectPerDay}
                                            />
                                        </div>
                                        {errors.injectPerDay && (
                                            <p className="text-danger">{errors.injectPerDay.message}</p>
                                        )}
                                    </FormGroup>
                                </Row>
                                <Row>
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="injectionTimes" className="fw-bold">
                                            Injection Times<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FormControl {...register("injectionTimes", {valueAsNumber: true})}
                                                         id="injectionTimes"
                                                         type="number"
                                                         min={1}
                                                         value={watch("injectionTimes")}
                                                         onKeyDown={blockInvalidChar}
                                                         isInvalid={!!errors.injectionTimes}
                                            />
                                        </div>
                                        {errors.injectionTimes && (
                                            <p className="text-danger">{errors.injectionTimes.message}</p>
                                        )}
                                    </FormGroup>
                                </Row>
                            </Col>
                            <Col md={9}>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="description" className="fw-bold">
                                        Note:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("description")}
                                                     id="description"
                                                     type="text"
                                                     as="textarea"
                                                     rows={4}
                                                     value={watch("description") ?? ""}
                                                     isInvalid={!!errors.description}
                                        />
                                    </div>
                                    {errors.description && (
                                        <p className="text-danger">{errors.description.message}</p>
                                    )}
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
                                <Link to={"/schedule/list"} id="cancelBtn" hidden></Link>
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
    )
}

export default UpdateSchedule