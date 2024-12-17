import { useEffect, useState } from "react";
import { Button, Card, Col, Container, Form, FormControl, FormGroup, FormSelect, Row } from "react-bootstrap";
import axios from "axios";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Link, useNavigate, useParams } from "react-router-dom";
import { SubmitHandler, useForm } from "react-hook-form";
import { Toast_Custom } from "../../Utils/Toast_Custom";
import { CreateResultSchema } from "../../Interface/ValidationSchema/ValidationSchemas";
import LoadingOverlay from "../../Utils/LoadingOverlay/LoadingOverlay";
import { Customer, InjectionResultData } from "../../Interface/BusinessObjectInterface";
import { showErrorAlert } from "../../Utils/ErrorAlert";
import BASE_URL from "../../Api/BaseApi";

type InputData = z.infer<typeof CreateResultSchema>;

const CreateResult = () => {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [data, setData] = useState<InjectionResultData>();
    const [customers, setCustomers] = useState<Customer[]>([]);
    const { scheduleId } = useParams<{ scheduleId: string }>();
    const [selectedCustomer, setSelectedCustomer] = useState<string[]>([]);
    const [fullAddress, setFullAddress] = useState<{ [key: string]: boolean }>({});
    const [selectAll, setSelectAll] = useState<boolean>(false);
    const token = localStorage.getItem("token");
    const nav = useNavigate();
    document.title = "Create Injection Result";

    useEffect(() => {
        setIsLoading(true);
        axios.get(`${BASE_URL}/vaccination/result/inject/${scheduleId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            }
        }).then(response => {
            const result = response.data;
            setData(result);
            setCustomers(result.customer);
        }).catch(error => {
            if (error.response) {
                showErrorAlert(() => {
                    nav("/");
                }, error.response.data.message)
            }
        }).finally(() => {
            setIsLoading(false);
        })

    }, [token, nav, scheduleId]);

    const handleReset = () => {
        // Reset only the 'customer' field
        setSelectAll(false);
        setSelectedCustomer([]);
    };

    const handleSubmit = () => {
        // Handle form submission logic here
        setIsLoading(true);
        try {
            axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                method: "POST",
                url: `${BASE_URL}/vaccination/result`,
                data: {
                    customerName: selectedCustomer,
                    scheduleId: scheduleId,
                    place: data?.schedule
                }
            })
                .then((response) => {
                    if (response.status === 200) {
                        Toast_Custom({
                            type: "success",
                            message: "Create injection result successfully",
                        })
                        setTimeout(() => {
                            nav("/result/list");
                        }, 500)
                    }
                })
        } catch
        (error) {
            console.error("Error creating injection result:", error);
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

    const handleCheckboxChange = (username: string) => {
        setSelectedCustomer(prevSelectedCustomers => {
            if (prevSelectedCustomers.includes(username)) {
                return prevSelectedCustomers.filter(c => c !== username);
            } else {
                return [...prevSelectedCustomers, username];
            }
        });
    }

    const toggleFullAddress = (username: string) => {
        setFullAddress(prevState => ({
            ...prevState,
            [username]: !prevState[username]
        }));
    };


    const handleSelectAllChange = () => {
        if (selectAll) {
            setSelectedCustomer([]);
        } else {
            setSelectedCustomer(customers.map(c => c.username))
        }
        setSelectAll(!selectAll);
    };
    console.log(selectedCustomer);

    return (
        <Container className='pt-4'>
            {isLoading ? <LoadingOverlay /> : null}
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>CREATE INJECTION RESULT</h5>
            <Container style={{ backgroundColor: "white" }} className='mb-5'>
                <Row className='py-3'>
                    <Col>
                        <FormGroup className="text-start">
                            <Form.Label htmlFor="prevention" className="fw-bold">
                                Prevention<span style={{ color: "red" }}>(*):</span>
                            </Form.Label>
                            <div className="d-flex align-items-center">
                                <FormControl
                                    id="prevention"
                                    type="text"
                                    readOnly
                                    disabled
                                    value={data?.vaccineName}
                                ></FormControl>
                            </div>
                        </FormGroup>
                    </Col>
                    <Col>
                        <FormGroup className="text-start">
                            <Form.Label htmlFor="vaccineType" className="fw-bold">
                                Vaccine type<span style={{ color: "red" }}>(*):</span>
                            </Form.Label>
                            <div className="d-flex align-items-center">
                                <FormControl
                                    id="vaccineType"
                                    type="text"
                                    readOnly
                                    disabled
                                    value={data?.vaccineType}
                                ></FormControl>
                            </div>
                        </FormGroup>
                    </Col>
                </Row>
                <Row className="py-3">
                    <Col>
                        <FormGroup className="text-start">
                            <Form.Label htmlFor="injection" className="fw-bold">
                                Injection:
                            </Form.Label>
                            <div className="d-flex align-items-center">
                                <FormControl
                                    id="injection"
                                    type="text"
                                    readOnly
                                    disabled
                                    value={data?.injection}
                                ></FormControl>
                            </div>
                        </FormGroup>
                    </Col>
                    <Col>
                        <FormGroup className="text-start">
                            <Form.Label htmlFor="dateOfInjection" className="fw-bold">
                                Date of injection:
                            </Form.Label>
                            <div className="d-flex align-items-center">
                                <FormControl
                                    id="dateOfInjection"
                                    type="date"
                                    readOnly
                                    disabled
                                    value={data?.injectionDate}
                                ></FormControl>
                            </div>
                        </FormGroup>
                    </Col>
                    <Col>
                        <FormGroup className="text-start">
                            <Form.Label htmlFor="nextInjectionAppointment" className="fw-bold">
                                Next injection appointment:
                            </Form.Label>
                            <div className="d-flex align-items-center">
                                <FormControl
                                    id="nextInjectionAppointment"
                                    type="date"
                                    readOnly
                                    disabled
                                    value={data?.injectionNextDate}
                                ></FormControl>
                            </div>
                        </FormGroup>
                    </Col>
                </Row>
                <Row className="py-3">
                    <Col>
                        <FormGroup className="text-start">
                            <Form.Label htmlFor="place" className="fw-bold">
                                Place of injection:
                            </Form.Label>
                            <div className="d-flex align-items-center">
                                <FormControl
                                    id="place"
                                    type="text"
                                    readOnly
                                    disabled
                                    value={data?.schedule}
                                ></FormControl>
                            </div>
                        </FormGroup>
                    </Col>
                </Row>
            </Container>
            <Container style={{ backgroundColor: "white" }}>
                <div style={{ textAlign: 'start' }} className='p-2'>LIST CUSTOMERS</div>

                {customers.length > 0 ?
                    <Container className='py-3'>
                        <table>
                            <thead>
                                <tr>
                                    <th><input type='checkbox' checked={selectAll} onChange={handleSelectAllChange} /></th>
                                    <th>Customer Name</th>
                                    <th>Date of Birth</th>
                                    <th>Gender</th>
                                    <th>Identity Card</th>
                                    <th>Phone</th>
                                    <th>Address</th>
                                </tr>
                            </thead>
                            <tbody className='text-start'>
                                {customers.map((r, index) => (
                                    <tr key={index}>
                                        <td className='text-center'><input type='checkbox' checked={selectedCustomer.includes(r.username)} onChange={() => handleCheckboxChange(r.username)} /></td>
                                        <td>{r.employeeName}</td>
                                        <td>{r.dateOfBirth}</td>
                                        <td>{r.gender}</td>
                                        <td>{r.identityCard}</td>
                                        <td>{r.phone}</td>
                                        <td style={{ width: '350px' }}>
                                            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                                {fullAddress[r.username] ? r.address : (r.address?.length > 30 ? `${r.address?.substring(0, 30)}...` : r.address)}
                                                {r.address?.length > 30 ? <i
                                                    className="fa fa-eye"
                                                    style={{ cursor: 'pointer' }}
                                                    onClick={() => toggleFullAddress(r.username)}
                                                    title={fullAddress[r.username] ? 'Hide address' : 'Show full address'}
                                                ></i> : null}
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        <Row className="mt-4 mb-4">
                            <Col className="d-flex justify-content-start">
                                <Button
                                    type="submit"
                                    variant="success"
                                    className="me-3 text-white rounded-0"
                                    style={{ width: "100px" }}
                                    onClick={handleSubmit}
                                >
                                    Save
                                </Button>
                                <Button
                                    type="reset"
                                    variant="info"
                                    className="me-3 text-white rounded-0"
                                    style={{ width: "100px" }}
                                    onClick={handleReset}
                                >
                                    Reset
                                </Button>
                                <Link
                                    to={"/result/list"}
                                    className={"btn btn-warning me-3 text-white rounded-0"}
                                    style={{ width: "100px" }}
                                >
                                    Cancel
                                </Link>
                            </Col>
                        </Row>
                    </Container>
                    : <h3>No data found!</h3>}
            </Container>
        </Container>
    );
};

export default CreateResult;
